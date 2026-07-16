package com.projects.job_tracker.domain.model;

import java.util.List;

public record JobListingGroup(
		String key,
		String label,
		int count,
		List<JobListing> jobs) {
}
