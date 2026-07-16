package com.projects.job_tracker.infrastructure.scraping.indeed;

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

public class IndeedJobFetcher {

	private static final Logger log = LoggerFactory.getLogger(IndeedJobFetcher.class);

	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
					+ "(KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

	private static final String RESULTS_SELECTOR =
			"#mosaic-jobResults div.job_seen_beacon, .mosaic-provider-jobcards div.job_seen_beacon";

	private static final String CARD_SELECTOR =
			"#mosaic-jobResults div.job_seen_beacon, .mosaic-provider-jobcards div.job_seen_beacon";

	private static final String RIGHT_PANE_SELECTOR = ".jobsearch-RightPane";

	private static final String DESCRIPTION_SELECTOR =
			".jobsearch-RightPane #jobDescriptionText, #jobDescriptionText, div.jobsearch-JobComponent-description";

	private final RateLimiter rateLimiter;
	private final ScrapingSettingsHolder settingsHolder;
	private final IndeedJobParser parser;

	public IndeedJobFetcher(
			RateLimiter rateLimiter,
			ScrapingSettingsHolder settingsHolder,
			IndeedJobParser parser) {
		this.rateLimiter = rateLimiter;
		this.settingsHolder = settingsHolder;
		this.parser = parser;
	}

	public List<ScrapedJob> scrapeJobs(String searchUrl, int maxResults) {
		rateLimiter.acquire();
		String storageStatePath = settingsHolder.indeedStorageStatePath();
		int pageTimeoutMs = settingsHolder.indeedPageTimeoutMs();
		boolean hasSession = storageStatePath != null && !storageStatePath.isBlank();

		log.info("Indeed scrape start: url={}, sessionConfigured={}, timeoutMs={}", searchUrl, hasSession, pageTimeoutMs);
		if (!hasSession) {
			log.warn("Indeed scrape without session file — Cloudflare or login may block results");
		}

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

				if (looksLikeChallenge(page)) {
					log.warn("Indeed challenge page detected: url={}, title={}", page.url(), page.title());
				}

				try {
					page.waitForSelector(
							RESULTS_SELECTOR,
							new Page.WaitForSelectorOptions().setTimeout(pageTimeoutMs));
				}
				catch (TimeoutError ex) {
					logPageState(page, "results selector timeout");
					return parser.parse(page.content(), maxResults);
				}

				List<ScrapedJob> summaries = parser.parse(page.content(), maxResults);
				log.info("Indeed mosaic parsed: summaries={}", summaries.size());

				if (summaries.isEmpty()) {
					logPageState(page, "no summaries");
					return summaries;
				}

				List<ScrapedJob> jobs = new ArrayList<>();
				for (ScrapedJob summary : summaries) {
					ScrapedJob job = scrapeCardDetail(page, summary, pageTimeoutMs);
					if (job != null) {
						jobs.add(job);
						log.debug("Indeed scraped job: title='{}', jk={}", job.title(), job.externalId());
					}
				}

				log.info("Indeed scrape done: pageUrl={}, jobsFound={}", page.url(), jobs.size());
				return jobs;
			}
		}
	}

	private ScrapedJob scrapeCardDetail(Page page, ScrapedJob summary, int pageTimeoutMs) {
		try {
			Locator cardLink = page.locator("a.jcs-JobTitle[data-jk='" + summary.externalId() + "']").first();
			cardLink.scrollIntoViewIfNeeded();
			cardLink.click();

			page.waitForSelector(
					DESCRIPTION_SELECTOR,
					new Page.WaitForSelectorOptions().setTimeout(pageTimeoutMs));

			Locator rightPane = page.locator(RIGHT_PANE_SELECTOR);
			String panelHtml = rightPane.count() > 0 ? rightPane.first().innerHTML() : page.content();
			return parser.enrichWithDetail(summary, panelHtml).orElse(summary);
		}
		catch (RuntimeException ex) {
			log.warn("Indeed card jk={} detail failed: {}", summary.externalId(), ex.getMessage());
			return summary;
		}
	}

	private void logPageState(Page page, String stage) {
		String html = page.content();
		log.info(
				"Indeed page [{}]: url={}, title={}, htmlLength={}, mosaicCards={}, jobLinks={}, looksLikeChallenge={}",
				stage,
				page.url(),
				page.title(),
				html.length(),
				page.locator(CARD_SELECTOR).count(),
				page.locator("a.jcs-JobTitle[data-jk]").count(),
				looksLikeChallenge(page));
	}

	private boolean looksLikeChallenge(Page page) {
		String title = page.title().toLowerCase();
		String url = page.url().toLowerCase();
		return title.contains("security") || title.contains("captcha") || url.contains("captcha");
	}
}
