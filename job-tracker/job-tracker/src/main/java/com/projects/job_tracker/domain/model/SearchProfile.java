package com.projects.job_tracker.domain.model;

public record SearchProfile(Long id, String name, String keywords, String filters) {

	public SearchProfile {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Profile name is required");
		}
		if (keywords == null || keywords.isBlank()) {
			throw new IllegalArgumentException("Profile keywords are required");
		}
	}
}
