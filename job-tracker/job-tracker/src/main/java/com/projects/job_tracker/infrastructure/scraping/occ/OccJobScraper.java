package com.projects.job_tracker.infrastructure.scraping.occ;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.domain.port.JobScraper;

@Component
public class OccJobScraper implements JobScraper {

	private static final Logger log = LoggerFactory.getLogger(OccJobScraper.class);

	private final OccJobFetcher fetcher;

	public OccJobScraper(OccJobFetcher fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public JobPlatform platform() {
		return JobPlatform.OCC;
	}

	@Override
	public List<ScrapedJob> scrape(ScrapeCriteria criteria) {
		String searchUrl = OccSearchUrlBuilder.build(criteria);
		log.info(
				"OCC scrape start: keywords='{}', location='{}', maxResults={}, searchUrl={}",
				criteria.keywords(),
				criteria.location(),
				criteria.maxResults(),
				searchUrl);

		List<ScrapedJob> jobs = fetcher.scrapeJobs(searchUrl, criteria.maxResults());
		log.info("OCC scrape done: jobsFound={}", jobs.size());
		return jobs;
	}
}
