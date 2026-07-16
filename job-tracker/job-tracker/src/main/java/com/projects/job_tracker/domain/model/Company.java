package com.projects.job_tracker.domain.model;

public record Company(Long id, String name, String website) {

	public Company {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Company name is required");
		}
	}

	public Company(String name, String website) {
		this(null, name, website);
	}
}
