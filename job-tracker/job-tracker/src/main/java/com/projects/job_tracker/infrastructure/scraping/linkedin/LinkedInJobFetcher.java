package com.projects.job_tracker.infrastructure.scraping.linkedin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.infrastructure.scraping.RateLimiter;
import com.projects.job_tracker.infrastructure.scraping.ScrapingSettingsHolder;

public class LinkedInJobFetcher {

	private final RateLimiter rateLimiter;
	private final ScrapingSettingsHolder settingsHolder;
	private final LinkedInJobParser parser;

	public LinkedInJobFetcher(
			RateLimiter rateLimiter,
			ScrapingSettingsHolder settingsHolder,
			LinkedInJobParser parser) {
		this.rateLimiter = rateLimiter;
		this.settingsHolder = settingsHolder;
		this.parser = parser;
	}

	public List<ScrapedJob> scrapeJobs(String searchUrl, int maxResults) {
		rateLimiter.acquire();
		String storageStatePath = settingsHolder.linkedinStorageStatePath();
		int pageTimeoutMs = settingsHolder.linkedinPageTimeoutMs();

		try (Playwright playwright = Playwright.create()) {
			Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
			Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
					.setUserAgent(
							"Mozilla/5.0 (compatible; JobTrackerBot/1.0; +https://localhost/job-tracker)");
			if (storageStatePath != null && !storageStatePath.isBlank()) {
				contextOptions.setStorageStatePath(Path.of(storageStatePath));
			}

			try (BrowserContext context = browser.newContext(contextOptions)) {
				Page page = context.newPage();
				page.setDefaultTimeout(pageTimeoutMs);
				page.navigate(searchUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
				page.waitForSelector(
						"ul.jobs-search__results-list, [class*='results-list'], [class*='jobs-search']",
						new Page.WaitForSelectorOptions().setTimeout(pageTimeoutMs));

				List<ScrapedJob> summaries = parser.parse(page.content(), maxResults);
				List<ScrapedJob> enriched = new ArrayList<>();
				for (ScrapedJob summary : summaries) {
					try {
						page.navigate(summary.url(), new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
						page.waitForSelector(
								".show-more-less-html__markup, .description__text, h1",
								new Page.WaitForSelectorOptions().setTimeout(pageTimeoutMs));
						ScrapedJob job = parser.enrichWithDetail(summary, page.content()).orElse(summary);
						enriched.add(job);
					}
					catch (RuntimeException ex) {
						enriched.add(summary);
					}
				}
				return enriched;
			}
		}
	}
}
