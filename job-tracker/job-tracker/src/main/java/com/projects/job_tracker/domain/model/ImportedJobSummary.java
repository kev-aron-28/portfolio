package com.projects.job_tracker.domain.model;

public record ImportedJobSummary(
		Long jobId,
		String title,
		String companyName,
		String source,
		String url,
		boolean duplicate) {
}
