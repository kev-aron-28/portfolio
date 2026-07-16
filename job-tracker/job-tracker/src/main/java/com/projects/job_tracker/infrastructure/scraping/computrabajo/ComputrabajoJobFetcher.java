package com.projects.job_tracker.infrastructure.scraping.computrabajo;

import java.nio.file.Path;
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

public class ComputrabajoJobFetcher {

	private static final Logger log = LoggerFactory.getLogger(ComputrabajoJobFetcher.class);

	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
					+ "(KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

	private static final String CARD_SELECTOR =
			"article.box_offer[data-offers-grid-offer-item-container], article.box_offer[data-id]";

	private static final String DETAIL_TITLE_SELECTOR = "[data-offers-grid-detail-title]";

	private static final String DETAIL_PANEL_SELECTOR = "[data-offers-grid-detail-container]";

	private final RateLimiter rateLimiter;
	private final ScrapingSettingsHolder settingsHolder;
	private final ComputrabajoJobParser parser;

	public ComputrabajoJobFetcher(
			RateLimiter rateLimiter,
			ScrapingSettingsHolder settingsHolder,
			ComputrabajoJobParser parser) {
		this.rateLimiter = rateLimiter;
		this.settingsHolder = settingsHolder;
		this.parser = parser;
	}

	public List<ScrapedJob> scrapeJobs(String searchUrl, int maxResults) {
		rateLimiter.acquire();
		String storageStatePath = settingsHolder.computrabajoStorageStatePath();
		int pageTimeoutMs = settingsHolder.computrabajoPageTimeoutMs();
		boolean hasSession = storageStatePath != null && !storageStatePath.isBlank();

		log.info(
				"Computrabajo scrape start: url={}, sessionConfigured={}, timeoutMs={}",
				searchUrl,
				hasSession,
				pageTimeoutMs);

		try (Playwright playwright = Playwright.create()) {
			Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
			Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
					.setUserAgent(USER_AGENT)
					.setLocale("es-MX");
			if (hasSession) {
				contextOptions.setStorageStatePath(Path.of(storageStatePath));
			}

			try (BrowserContext context = browser.newContext(contextOptions)) {
				Page page = context.newPage();
				page.setDefaultTimeout(pageTimeoutMs);
				page.navigate(searchUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

				try {
					page.waitForSelector(
							CARD_SELECTOR,
							new Page.WaitForSelectorOptions().setTimeout(pageTimeoutMs));
				}
				catch (TimeoutError ex) {
					logPageState(page, "card selector timeout");
					return parser.parse(page.content(), maxResults);
				}

				List<ScrapedJob> summaries = parser.parse(page.content(), maxResults);
				log.info("Computrabajo list parsed: summaries={}", summaries.size());
				if (summaries.isEmpty()) {
					logPageState(page, "no summaries");
					return summaries;
				}

				List<ScrapedJob> jobs = new ArrayList<>();
				Locator cards = page.locator(CARD_SELECTOR);
				int limit = Math.min(cards.count(), summaries.size());
				for (int index = 0; index < limit; index++) {
					ScrapedJob summary = summaries.get(index);
					ScrapedJob job = scrapeCardDetail(page, cards, index, summary, pageTimeoutMs);
					if (job != null) {
						jobs.add(job);
					}
				}

				log.info("Computrabajo scrape done: pageUrl={}, jobsFound={}", page.url(), jobs.size());
				return jobs;
			}
		}
	}

	private ScrapedJob scrapeCardDetail(
			Page page,
			Locator cards,
			int index,
			ScrapedJob summary,
			int pageTimeoutMs) {
		try {
			Locator card = cards.nth(index);
			card.scrollIntoViewIfNeeded();
			card.click();

			page.waitForSelector(
					DETAIL_TITLE_SELECTOR,
					new Page.WaitForSelectorOptions().setTimeout(pageTimeoutMs));

			Locator detailPanel = page.locator(DETAIL_PANEL_SELECTOR);
			String panelHtml = detailPanel.count() > 0 ? detailPanel.first().innerHTML() : page.content();
			return parser.enrichWithDetail(summary, panelHtml).orElse(summary);
		}
		catch (RuntimeException ex) {
			log.warn("Computrabajo card id={} detail failed: {}", summary.externalId(), ex.getMessage());
			return summary;
		}
	}

	private void logPageState(Page page, String stage) {
		String html = page.content();
		log.info(
				"Computrabajo page [{}]: url={}, title={}, htmlLength={}, cards={}, offerLinks={}",
				stage,
				page.url(),
				page.title(),
				html.length(),
				page.locator(CARD_SELECTOR).count(),
				page.locator("a.js-o-link[href*='oferta-de-trabajo']").count());
	}
}
