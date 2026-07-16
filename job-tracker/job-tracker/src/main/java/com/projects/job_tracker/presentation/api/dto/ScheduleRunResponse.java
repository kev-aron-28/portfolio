package com.projects.job_tracker.presentation.api.dto;

import java.util.List;

import com.projects.job_tracker.application.automation.RunScheduledScrapingUseCase;

public record ScheduleRunResponse(
		Long scheduleId,
		Long profileId,
		String profileName,
		int scraped,
		int imported,
		int duplicates,
		List<String> errors) {

	public static ScheduleRunResponse from(RunScheduledScrapingUseCase.ScheduleRunResult result) {
		return new ScheduleRunResponse(
				result.scheduleId(),
				result.profileId(),
				result.profileName(),
				result.scraped(),
				result.imported(),
				result.duplicates(),
				result.errors());
	}
}
