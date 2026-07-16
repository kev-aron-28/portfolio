package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;

public record JobFilterCriteria(
		String keyword,
		String source,
		String location,
		String companyName,
		BigDecimal minSalary,
		BigDecimal maxSalary,
		String workMode,
		String employmentType,
		String category,
		ApplicationStatus applicationStatus,
		boolean onlyUnapplied,
		JobSortField sortBy,
		SortDirection sortDirection) {

	public static JobFilterCriteria empty() {
		return new JobFilterCriteria(
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				JobSortField.CREATED_AT,
				SortDirection.DESC);
	}
}
