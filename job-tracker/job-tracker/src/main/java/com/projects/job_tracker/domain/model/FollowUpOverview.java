package com.projects.job_tracker.domain.model;

import java.util.List;

public record FollowUpOverview(
		long totalJobs,
		long applied,
		long active,
		long watch,
		long likelySilent,
		long closed,
		List<FollowUpEntry> attention,
		List<FollowUpDay> days) {
}
