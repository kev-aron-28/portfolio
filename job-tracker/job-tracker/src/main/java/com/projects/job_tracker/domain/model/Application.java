package com.projects.job_tracker.domain.model;

import java.time.Instant;

public record Application(
		Long id,
		Long jobId,
		ApplicationStatus status,
		Instant appliedAt,
		Instant statusChangedAt,
		String notes) {

	public Application {
		if (jobId == null) {
			throw new IllegalArgumentException("Job id is required");
		}
		if (status == null) {
			throw new IllegalArgumentException("Application status is required");
		}
	}

	public Instant statusSince() {
		return statusChangedAt != null ? statusChangedAt : appliedAt;
	}
}
