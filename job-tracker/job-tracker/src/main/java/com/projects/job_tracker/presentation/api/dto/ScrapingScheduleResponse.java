package com.projects.job_tracker.presentation.api.dto;

import java.time.Instant;
import java.util.List;

import com.projects.job_tracker.domain.model.ScrapingSchedule;

public record ScrapingScheduleResponse(
		Long id,
		Long profileId,
		List<String> platforms,
		int intervalMinutes,
		int maxResults,
		boolean enabled,
		Instant lastRunAt,
		Instant createdAt) {

	public static ScrapingScheduleResponse from(ScrapingSchedule schedule) {
		return new ScrapingScheduleResponse(
				schedule.id(),
				schedule.profileId(),
				schedule.platforms().stream().map(platform -> platform.source()).toList(),
				schedule.intervalMinutes(),
				schedule.maxResults(),
				schedule.enabled(),
				schedule.lastRunAt(),
				schedule.createdAt());
	}
}
