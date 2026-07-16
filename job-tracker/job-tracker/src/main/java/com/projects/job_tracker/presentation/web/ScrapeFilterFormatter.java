package com.projects.job_tracker.presentation.web;

import java.util.ArrayList;
import java.util.List;

import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.infrastructure.scraping.ScrapeFilterSetParser;

public final class ScrapeFilterFormatter {

	private ScrapeFilterFormatter() {
	}

	public static List<String> toLabels(String filtersJson) {
		return toLabels(ScrapeFilterSetParser.parse(filtersJson));
	}

	public static List<String> toLabels(ScrapeFilterSet filters) {
		if (filters == null || filters.isEmpty()) {
			return List.of();
		}
		List<String> labels = new ArrayList<>();
		if (filters.location() != null && !filters.location().isBlank()) {
			labels.add(filters.location());
		}
		if (filters.workMode() != null && !filters.workMode().isBlank()) {
			labels.add(formatWorkMode(filters.workMode()));
		}
		if (filters.salaryMin() != null || filters.salaryMax() != null) {
			labels.add(formatSalary(filters.salaryMin(), filters.salaryMax()));
		}
		if (filters.employmentType() != null && !filters.employmentType().isBlank()) {
			labels.add(filters.employmentType());
		}
		if (filters.postedWithinDays() != null) {
			labels.add("últimos " + filters.postedWithinDays() + " días");
		}
		return labels;
	}

	private static String formatWorkMode(String workMode) {
		return switch (workMode.toLowerCase()) {
			case "remote" -> "Remoto";
			case "hybrid" -> "Híbrido";
			case "onsite" -> "Presencial";
			default -> workMode;
		};
	}

	private static String formatSalary(java.math.BigDecimal min, java.math.BigDecimal max) {
		if (min != null && max != null) {
			return min.toPlainString() + " – " + max.toPlainString();
		}
		if (min != null) {
			return "desde " + min.toPlainString();
		}
		return "hasta " + max.toPlainString();
	}
}
