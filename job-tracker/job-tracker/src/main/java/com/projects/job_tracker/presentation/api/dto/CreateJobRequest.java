package com.projects.job_tracker.presentation.api.dto;

import java.math.BigDecimal;

public record CreateJobRequest(
		String title,
		String companyName,
		String companyWebsite,
		String description,
		String location,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String source,
		String url) {
}
