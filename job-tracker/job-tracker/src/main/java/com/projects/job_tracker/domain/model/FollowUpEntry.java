package com.projects.job_tracker.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public record FollowUpEntry(
		Long jobId,
		String title,
		String companyName,
		LocalDate registeredOn,
		Instant registeredAt,
		ApplicationStatus status,
		long daysInStatus,
		String daysLabel,
		FollowUpSignal signal,
		String insight) {
}
