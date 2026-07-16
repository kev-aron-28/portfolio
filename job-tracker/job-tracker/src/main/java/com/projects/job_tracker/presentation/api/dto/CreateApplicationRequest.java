package com.projects.job_tracker.presentation.api.dto;

import java.time.Instant;

import com.projects.job_tracker.domain.model.ApplicationStatus;

public record CreateApplicationRequest(
		Long jobId,
		ApplicationStatus status,
		Instant appliedAt,
		String notes) {
}
