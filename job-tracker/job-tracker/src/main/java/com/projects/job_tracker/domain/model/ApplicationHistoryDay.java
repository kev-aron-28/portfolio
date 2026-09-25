package com.projects.job_tracker.domain.model;

import java.time.LocalDate;
import java.util.List;

public record ApplicationHistoryDay(
		LocalDate date,
		String label,
		List<ApplicationHistoryItem> items) {
}
