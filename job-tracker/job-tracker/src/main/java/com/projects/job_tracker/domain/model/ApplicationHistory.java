package com.projects.job_tracker.domain.model;

import java.util.List;

public record ApplicationHistory(
		long total,
		List<ApplicationHistoryMonth> months) {
}
