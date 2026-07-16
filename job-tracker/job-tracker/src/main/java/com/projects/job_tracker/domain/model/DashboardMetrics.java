package com.projects.job_tracker.domain.model;

import java.util.Map;

public record DashboardMetrics(
		long totalJobs,
		long totalApplications,
		long jobsLast7Days,
		long applicationsLast7Days,
		Map<String, Long> jobsBySource,
		Map<ApplicationStatus, Long> applicationsByStatus,
		MarketInsights market) {
}
