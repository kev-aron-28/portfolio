package com.projects.job_tracker.infrastructure.scraping.computrabajo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.domain.port.JobScraper;

@Component
public class ComputrabajoJobScraper implements JobScraper {

	private static final Logger log = LoggerFactory.getLogger(ComputrabajoJobScraper.class);

	private final ComputrabajoJobFetcher fetcher;

	public ComputrabajoJobScraper(ComputrabajoJobFetcher fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public JobPlatform platform() {
		return JobPlatform.COMPUTRABAJO;
	}

	@Override
	public List<ScrapedJob> scrape(ScrapeCriteria criteria) {
		String searchUrl = ComputrabajoSearchUrlBuilder.build(criteria);
		log.info(
				"Computrabajo scrape start: keywords='{}', location='{}', maxResults={}, searchUrl={}",
				criteria.keywords(),
				criteria.location(),
				criteria.maxResults(),
				searchUrl);

		List<ScrapedJob> jobs = fetcher.scrapeJobs(searchUrl, criteria.maxResults());
		log.info("Computrabajo scrape done: jobsFound={}", jobs.size());
		return jobs;
	}
}
