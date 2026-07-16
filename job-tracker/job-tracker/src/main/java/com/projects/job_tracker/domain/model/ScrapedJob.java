package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ScrapedJob(
		JobPlatform platform,
		String externalId,
		String title,
		String companyName,
		String description,
		String location,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String url,
		Instant postedAt,
		String employmentType,
		String workMode,
		String category,
		String subcategory,
		String benefits,
		String requirements) {

	public ScrapedJob {
		if (platform == null) {
			throw new IllegalArgumentException("Platform is required");
		}
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Title is required");
		}
		if (url == null || url.isBlank()) {
			throw new IllegalArgumentException("Url is required");
		}
	}
}
