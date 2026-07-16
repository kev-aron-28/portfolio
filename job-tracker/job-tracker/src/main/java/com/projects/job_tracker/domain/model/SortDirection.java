package com.projects.job_tracker.domain.model;

public enum SortDirection {
	ASC,
	DESC;

	public static SortDirection fromParam(String value) {
		if (value == null || value.isBlank()) {
			return DESC;
		}
		try {
			return SortDirection.valueOf(value.trim().toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			return DESC;
		}
	}
}
