package com.projects.job_tracker.infrastructure.scraping.indeed;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.domain.port.JobScraper;

@Component
public class IndeedJobScraper implements JobScraper {

	private static final Logger log = LoggerFactory.getLogger(IndeedJobScraper.class);

	private final IndeedJobFetcher fetcher;

	public IndeedJobScraper(IndeedJobFetcher fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public JobPlatform platform() {
		return JobPlatform.INDEED;
	}

	@Override
	public List<ScrapedJob> scrape(ScrapeCriteria criteria) {
		String searchUrl = IndeedSearchUrlBuilder.build(criteria);
		log.info(
				"Indeed scrape start: keywords='{}', location='{}', maxResults={}, searchUrl={}",
				criteria.keywords(),
				criteria.location(),
				criteria.maxResults(),
				searchUrl);

		List<ScrapedJob> jobs = fetcher.scrapeJobs(searchUrl, criteria.maxResults());
		log.info("Indeed scrape done: jobsFound={}", jobs.size());
		return jobs;
	}
}
