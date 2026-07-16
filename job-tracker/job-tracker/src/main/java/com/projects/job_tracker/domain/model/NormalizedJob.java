package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record NormalizedJob(
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
		String requirements) {

	public NormalizedJob {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Title is required");
		}
		if (companyName == null || companyName.isBlank()) {
			throw new IllegalArgumentException("Company name is required");
		}
		if (source == null || source.isBlank()) {
			throw new IllegalArgumentException("Source is required");
		}
		if (url == null || url.isBlank()) {
			throw new IllegalArgumentException("Url is required");
		}
	}
}
