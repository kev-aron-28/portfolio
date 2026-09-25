package com.projects.job_tracker.presentation.api.dto;

import java.time.Instant;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;

public record ApplicationResponse(
		Long id,
		Long jobId,
		ApplicationStatus status,
		Instant appliedAt,
		Instant statusChangedAt,
		String notes) {

	public static ApplicationResponse from(Application application) {
		return new ApplicationResponse(
				application.id(),
				application.jobId(),
				application.status(),
				application.appliedAt(),
				application.statusChangedAt(),
				application.notes());
	}
}
