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
		Long segmentId,
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
				null,
				JobSortField.CREATED_AT,
				SortDirection.DESC);
	}
}
