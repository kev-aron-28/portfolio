package com.projects.job_tracker.testutil;

import java.math.BigDecimal;
import java.time.Instant;

import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapedJob;

public final class TestJobs {

	private TestJobs() {
	}

	public static ScrapedJob scraped(
			JobPlatform platform,
			String externalId,
			String title,
			String company,
			String location,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			String url) {
		return new ScrapedJob(
				platform,
				externalId,
				title,
				company,
				null,
				location,
				salaryMin,
				salaryMax,
				url,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}

	public static Job job(
			Long id,
			String title,
			Long companyId,
			String description,
			String location,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			String source,
			String url,
			Instant createdAt) {
		return new Job(
				id,
				title,
				companyId,
				description,
				location,
				salaryMin,
				salaryMax,
				source,
				url,
				createdAt,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}

	public static CreateJobUseCase.CreateJobCommand createCommand(
			String title,
			String companyName,
			String companyWebsite,
			String description,
			String location,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			String source,
			String url) {
		return new CreateJobUseCase.CreateJobCommand(
				title,
				companyName,
				companyWebsite,
				description,
				location,
				salaryMin,
				salaryMax,
				source,
				url,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}

	public static ScrapeJobsUseCase.ScrapeJobsCommand scrapeCommand(
			Long profileId,
			String keywords,
			String location,
			java.util.List<JobPlatform> platforms,
			int maxResults) {
		return new ScrapeJobsUseCase.ScrapeJobsCommand(
				profileId,
				keywords,
				location,
				null,
				null,
				null,
				null,
				null,
				platforms,
				maxResults);
	}

	public static JobListing listing(
			Long id,
			String title,
			String companyName,
			String location,
			String source,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			Instant createdAt,
			String url,
			com.projects.job_tracker.domain.model.ApplicationStatus status) {
		return new JobListing(
				id,
				title,
				companyName,
				location,
				source,
				salaryMin,
				salaryMax,
				createdAt,
				null,
				null,
				null,
				null,
				null,
				null,
				url,
				status);
	}
}
