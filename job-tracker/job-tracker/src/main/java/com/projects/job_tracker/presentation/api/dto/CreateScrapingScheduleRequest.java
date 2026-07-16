package com.projects.job_tracker.presentation.api.dto;

import java.time.Instant;
import java.util.List;

import com.projects.job_tracker.application.automation.CreateScrapingScheduleUseCase;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSchedule;

public record CreateScrapingScheduleRequest(
		Long profileId, List<String> platforms, Integer intervalMinutes, Integer maxResults) {

	public CreateScrapingScheduleUseCase.CreateScrapingScheduleCommand toCommand() {
		List<JobPlatform> resolvedPlatforms = platforms == null || platforms.isEmpty()
				? List.of(JobPlatform.OCC, JobPlatform.LINKEDIN)
				: platforms.stream().map(JobPlatform::fromSource).toList();
		int resolvedInterval = intervalMinutes == null ? 60 : intervalMinutes;
		int resolvedMaxResults = maxResults == null ? 20 : maxResults;
		return new CreateScrapingScheduleUseCase.CreateScrapingScheduleCommand(
				profileId, resolvedPlatforms, resolvedInterval, resolvedMaxResults);
	}
}
