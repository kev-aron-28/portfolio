package com.projects.job_tracker.domain.model;

import java.util.List;
import java.util.Map;

public record JobListingOverview(
		int total,
		int unapplied,
		int applied,
		Map<String, Integer> bySource,
		Map<String, Integer> byWorkMode,
		List<JobListingGroup> groups) {
}
