package com.projects.job_tracker.infrastructure.scraping;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.port.JobScraper;

@Component
public class JobScraperRegistry {

	private final Map<JobPlatform, JobScraper> scrapersByPlatform;

	public JobScraperRegistry(List<JobScraper> scrapers) {
		this.scrapersByPlatform =
				scrapers.stream().collect(Collectors.toMap(JobScraper::platform, Function.identity()));
	}

	public JobScraper get(JobPlatform platform) {
		JobScraper scraper = scrapersByPlatform.get(platform);
		if (scraper == null) {
			throw new IllegalArgumentException("No scraper registered for platform: " + platform);
		}
		return scraper;
	}
}
