package com.projects.job_tracker.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public record ApplicationHistoryItem(
		Long jobId,
		String title,
		String companyName,
		String source,
		ApplicationStatus status,
		LocalDate appliedOn,
		Instant appliedAt) {
}
