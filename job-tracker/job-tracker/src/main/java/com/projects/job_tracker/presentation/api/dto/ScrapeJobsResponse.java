package com.projects.job_tracker.presentation.api.dto;

import java.util.List;

import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;

public record ScrapeJobsResponse(int scraped, int imported, int duplicates, List<String> errors) {

	public static ScrapeJobsResponse from(ScrapeJobsUseCase.ScrapeResult result) {
		return new ScrapeJobsResponse(result.scraped(), result.imported(), result.duplicates(), result.errors());
	}
}
