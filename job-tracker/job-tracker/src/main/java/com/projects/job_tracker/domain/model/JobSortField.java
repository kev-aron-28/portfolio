package com.projects.job_tracker.domain.model;

public enum JobSortField {
	CREATED_AT("createdAt"),
	POSTED_AT("postedAt"),
	TITLE("title"),
	SALARY_MIN("salaryMin"),
	SALARY_MAX("salaryMax"),
	COMPANY("company.name");

	private final String property;

	JobSortField(String property) {
		this.property = property;
	}

	public String property() {
		return property;
	}

	public static JobSortField fromParam(String value) {
		if (value == null || value.isBlank()) {
			return CREATED_AT;
		}
		try {
			return JobSortField.valueOf(value.trim().toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			return CREATED_AT;
		}
	}
}
