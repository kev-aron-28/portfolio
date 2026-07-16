package com.projects.job_tracker.presentation.api.dto;

import com.projects.job_tracker.domain.model.ApplicationStatus;

public record UpdateApplicationRequest(ApplicationStatus status, String notes) {
}
