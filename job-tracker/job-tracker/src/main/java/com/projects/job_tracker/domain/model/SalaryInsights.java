package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;
import java.util.Map;

public record SalaryInsights(
		long jobsWithSalary,
		long jobsWithoutSalary,
		BigDecimal averageMidpoint,
		BigDecimal medianMidpoint,
		BigDecimal minSalary,
		BigDecimal maxSalary,
		Map<String, BigDecimal> averageByWorkMode) {

	public static SalaryInsights empty(long totalJobs) {
		return new SalaryInsights(0, totalJobs, null, null, null, null, Map.of());
	}
}
