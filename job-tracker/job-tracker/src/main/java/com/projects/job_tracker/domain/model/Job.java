package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Job(
		Long id,
		String title,
		Long companyId,
		String description,
		String location,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String source,
		String url,
		Instant createdAt,
		String externalId,
		Instant postedAt,
		String employmentType,
		String workMode,
		String category,
		String subcategory,
		String benefits,
		String requirements) {

	public Job {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Job title is required");
		}
		if (companyId == null) {
			throw new IllegalArgumentException("Company id is required");
		}
		if (source == null || source.isBlank()) {
			throw new IllegalArgumentException("Job source is required");
		}
		if (url == null || url.isBlank()) {
			throw new IllegalArgumentException("Job url is required");
		}
	}
}
