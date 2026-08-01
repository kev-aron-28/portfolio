package com.projects.job_tracker.presentation.api.dto;

import java.util.List;

public record BulkDeleteJobsRequest(List<Long> ids) {
}
