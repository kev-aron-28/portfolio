package com.projects.job_tracker.domain.model;

import java.util.List;

public record SearchReport(
		String headline,
		List<ReportFinding> findings,
		List<ReportMetric> snapshot,
		List<ReportShare> byStatus,
		List<ReportWait> waitByStatus,
		List<ReportShare> weeklyApplications,
		List<ReportShare> appliedBySource,
		List<ReportShare> appliedByWorkMode,
		List<ReportShare> appliedByLocation,
		boolean hasApplications) {

	public static SearchReport empty() {
		return new SearchReport(
				"Aún no hay postulaciones suficientes para un análisis.",
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				false);
	}
}
