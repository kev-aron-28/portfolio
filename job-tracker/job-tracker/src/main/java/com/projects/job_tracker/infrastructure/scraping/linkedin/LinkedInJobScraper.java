package com.projects.job_tracker.infrastructure.scraping.linkedin;

import java.util.List;

import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.domain.port.JobScraper;

@Component
public class LinkedInJobScraper implements JobScraper {

	private final LinkedInJobFetcher fetcher;

	public LinkedInJobScraper(LinkedInJobFetcher fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public JobPlatform platform() {
		return JobPlatform.LINKEDIN;
	}

	@Override
	public List<ScrapedJob> scrape(ScrapeCriteria criteria) {
		String searchUrl = LinkedInSearchUrlBuilder.build(criteria);
		return fetcher.scrapeJobs(searchUrl, criteria.maxResults());
	}
}
