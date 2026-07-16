package com.projects.job_tracker.domain.model;

public enum JobListViewMode {
	GROUPED("grouped"),
	TABLE("table");

	private final String param;

	JobListViewMode(String param) {
		this.param = param;
	}

	public String param() {
		return param;
	}

	public static JobListViewMode fromParam(String value) {
		if (value == null || value.isBlank()) {
			return GROUPED;
		}
		for (JobListViewMode mode : values()) {
			if (mode.param.equalsIgnoreCase(value) || mode.name().equalsIgnoreCase(value)) {
				return mode;
			}
		}
		return GROUPED;
	}
}
