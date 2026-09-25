package com.projects.job_tracker.domain.model;

import java.time.YearMonth;
import java.util.List;

public record ApplicationHistoryMonth(
		YearMonth month,
		String label,
		int count,
		List<ApplicationHistoryDay> days) {
}
