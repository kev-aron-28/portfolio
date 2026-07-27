package com.projects.job_tracker.presentation.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.projects.job_tracker.application.job.CreateJobUseCase;

public record CreateJobRequest(
		String title,
		String companyName,
		String companyWebsite,
		String description,
		String location,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String source,
		String url,
		String externalId,
		Instant postedAt,
		String employmentType,
		String workMode,
		String category,
		String subcategory,
		String benefits,
		String requirements,
		List<Long> segmentIds) {

	public CreateJobUseCase.CreateJobCommand toCommand() {
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
				externalId,
				postedAt,
				employmentType,
				workMode,
				category,
				subcategory,
				benefits,
				requirements);
	}
}
