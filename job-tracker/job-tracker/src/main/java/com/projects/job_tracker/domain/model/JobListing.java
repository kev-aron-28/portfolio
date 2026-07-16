package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record JobListing(
		Long id,
		String title,
		String companyName,
		String location,
		String source,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		Instant createdAt,
		Instant postedAt,
		String workMode,
		String employmentType,
		String category,
		String requirements,
		String description,
		String url,
		ApplicationStatus applicationStatus) {
}
