package com.projects.job_tracker.domain.model;

import java.time.LocalDate;
import java.util.List;

public record FollowUpDay(
		LocalDate date,
		String label,
		int jobCount,
		int attentionCount,
		List<FollowUpEntry> entries) {
}
