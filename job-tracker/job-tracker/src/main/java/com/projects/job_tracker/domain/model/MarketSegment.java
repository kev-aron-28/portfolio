package com.projects.job_tracker.domain.model;

import java.time.Instant;

public record MarketSegment(
		Long id,
		String name,
		String description,
		String keywords,
		String filters,
		Instant createdAt) {

	public MarketSegment {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Segment name is required");
		}
	}
}
