package com.projects.job_tracker.domain.model;

import java.util.List;
import java.util.Map;

public record MarketInsights(
		SalaryInsights salary,
		long distinctCompanies,
		Map<String, Long> jobsByWorkMode,
		Map<String, Long> jobsByCategory,
		Map<String, Long> jobsByEmploymentType,
		Map<String, Long> jobsByExperienceLevel,
		List<RankedCount> topCompanies,
		List<RankedCount> topTechnologies,
		List<RankedCount> topLocations) {

	public static MarketInsights empty(long totalJobs) {
		return new MarketInsights(
				SalaryInsights.empty(totalJobs),
				0,
				Map.of(),
				Map.of(),
				Map.of(),
				Map.of(),
				List.of(),
				List.of(),
				List.of());
	}
}
