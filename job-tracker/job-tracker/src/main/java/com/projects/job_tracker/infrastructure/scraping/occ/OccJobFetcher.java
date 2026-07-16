package com.projects.job_tracker.infrastructure.scraping.occ;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitUntilState;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.infrastructure.scraping.RateLimiter;
import com.projects.job_tracker.infrastructure.scraping.ScrapingSettingsHolder;

public class OccJobFetcher {

	private static final Logger log = LoggerFactory.getLogger(OccJobFetcher.class);

	private static final String USER_AGENT =
			"Mozilla/5.0 (compatible; JobTrackerBot/1.0; +https://localhost/job-tracker)";

	private static final String CARD_SELECTOR = ".card-job-offer";
	private static final String DETAIL_TITLE_SELECTOR = "[data-offers-grid-detail-title]";
	private static final String DETAIL_PANEL_SELECTOR = "[data-offer-content]";

	private final RateLimiter rateLimiter;
	private final ScrapingSettingsHolder settingsHolder;
	private final OccJobParser parser;

	public OccJobFetcher(RateLimiter rateLimiter, ScrapingSettingsHolder settingsHolder, OccJobParser parser) {
		this.rateLimiter = rateLimiter;
		this.settingsHolder = settingsHolder;
		this.parser = parser;
	}

	public List<ScrapedJob> scrapeJobs(String url, int maxResults) {
		rateLimiter.acquire();
		String storageStatePath = settingsHolder.occStorageStatePath();
		int pageTimeoutMs = settingsHolder.occPageTimeoutMs();
		boolean hasSession = storageStatePath != null && !storageStatePath.isBlank();

		log.info("OCC scrape start: url={}, sessionConfigured={}, timeoutMs={}", url, hasSession, pageTimeoutMs);
		if (!hasSession) {
			log.warn("OCC scrape without session file — results may be empty (login required)");
		}
		else {
			Path sessionPath = Paths.get(storageStatePath);
			log.debug("OCC session path: exists={}", java.nio.file.Files.exists(sessionPath));
		}

		try (Playwright playwright = Playwright.create()) {
			Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
			Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setUserAgent(USER_AGENT);
			if (hasSession) {
				contextOptions.setStorageStatePath(Path.of(storageStatePath));
			}

			try (BrowserContext context = browser.newContext(contextOptions)) {
				Page page = context.newPage();
				page.setDefaultTimeout(pageTimeoutMs);
				page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
				logPageState(page, "after navigate");

				Locator cards = page.locator(CARD_SELECTOR);
				try {
					cards.first().waitFor(new Locator.WaitForOptions().setTimeout(pageTimeoutMs));
				}
				catch (TimeoutError ex) {
					logPageState(page, "card selector timeout");
					String html = page.content();
					List<ScrapedJob> fallback = parser.parse(html, maxResults);
					log.info("OCC fallback parse from page html: jobs={}", fallback.size());
					return fallback;
				}

				int cardCount = cards.count();
				int limit = Math.min(cardCount, maxResults);
				log.info("OCC cards found: total={}, processing={}", cardCount, limit);

				List<ScrapedJob> jobs = new ArrayList<>();
				for (int index = 0; index < limit; index++) {
					ScrapedJob job = scrapeCard(page, cards, index, pageTimeoutMs);
					if (job != null) {
						jobs.add(job);
						log.debug("OCC scraped job [{}]: title='{}', url={}", index, job.title(), job.url());
					}
				}

				log.info("OCC scrape done: pageUrl={}, jobsFound={}", page.url(), jobs.size());
				return jobs;
			}
		}
	}

	private ScrapedJob scrapeCard(Page page, Locator cards, int index, int pageTimeoutMs) {
		try {
			Locator card = cards.nth(index);
			card.scrollIntoViewIfNeeded();
			card.click();
			page.waitForSelector(
					DETAIL_TITLE_SELECTOR,
					new Page.WaitForSelectorOptions().setTimeout(pageTimeoutMs));

			String detailHtml = page.locator(DETAIL_PANEL_SELECTOR).innerHTML();
			return parser.parseDetailPanel(detailHtml).orElse(null);
		}
		catch (RuntimeException ex) {
			log.warn("OCC card [{}] scrape failed: {}", index, ex.getMessage());
			return null;
		}
	}

	private void logPageState(Page page, String stage) {
		String html = page.content();
		log.info(
				"OCC page [{}]: url={}, title={}, htmlLength={}, cardJobOffers={}, offerLinks={}, looksLikeLogin={}",
				stage,
				page.url(),
				page.title(),
				html.length(),
				page.locator(CARD_SELECTOR).count(),
				page.locator("a[href*='/empleo/oferta/'], a[href*='/empleos/oferta/']").count(),
				looksLikeLoginPage(page.url(), html));
	}

	private static boolean looksLikeLoginPage(String url, String html) {
		String lowerUrl = url.toLowerCase();
		String lowerHtml = html.toLowerCase();
		return lowerUrl.contains("login")
				|| lowerUrl.contains("account/login")
				|| lowerHtml.contains("iniciar sesión")
				|| lowerHtml.contains("iniciar sesion");
	}
}
