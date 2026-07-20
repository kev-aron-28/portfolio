package com.projects.job_tracker.presentation.api.dto;

import java.math.BigDecimal;
import java.util.List;

import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.domain.model.JobPlatform;

public record ScrapeJobsRequest(
		Long profileId,
		String keywords,
		String location,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String employmentType,
		String workMode,
		Integer postedWithinDays,
		List<String> platforms,
		Integer maxResults,
		Long segmentId) {

	public ScrapeJobsUseCase.ScrapeJobsCommand toCommand() {
		List<JobPlatform> resolvedPlatforms = platforms == null || platforms.isEmpty()
				? List.of(JobPlatform.OCC, JobPlatform.LINKEDIN)
				: platforms.stream().map(JobPlatform::fromSource).toList();

		int resolvedMaxResults = maxResults == null ? 20 : maxResults;
		return new ScrapeJobsUseCase.ScrapeJobsCommand(
				profileId,
				keywords,
				location,
				salaryMin,
				salaryMax,
				employmentType,
				workMode,
				postedWithinDays,
				resolvedPlatforms,
				resolvedMaxResults,
				segmentId);
	}
}
