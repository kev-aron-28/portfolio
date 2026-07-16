package com.projects.job_tracker.application.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.projects.job_tracker.domain.model.MarketInsights;
import com.projects.job_tracker.domain.model.RankedCount;
import com.projects.job_tracker.domain.model.SalaryInsights;

public final class MarketInsightsBuilder {

	private static final int TOP_LIMIT = 8;

	private MarketInsightsBuilder() {
	}

	public static MarketInsights build(
			long totalJobs,
			long distinctCompanies,
			List<Object[]> workModeRows,
			List<Object[]> categoryRows,
			List<Object[]> employmentTypeRows,
			List<Object[]> companyRows,
			List<Object[]> locationRows,
			List<Object[]> salaryRows,
			List<Object[]> textRows) {
		if (totalJobs == 0) {
			return MarketInsights.empty(0);
		}

		List<MarketTextAnalyzer.JobTextFields> textFields = textRows.stream()
				.map(row -> new MarketTextAnalyzer.JobTextFields(
						(String) row[0],
						(String) row[1],
						(String) row[2]))
				.toList();

		return new MarketInsights(
				buildSalaryInsights(totalJobs, salaryRows),
				distinctCompanies,
				labelCountMap(workModeRows, MarketInsightsBuilder::formatWorkMode),
				labelCountMap(categoryRows, value -> normalize(value, "Sin categoría")),
				labelCountMap(employmentTypeRows, MarketInsightsBuilder::formatEmploymentType),
				MarketTextAnalyzer.countExperienceLevels(textFields),
				toRankedList(companyRows, TOP_LIMIT),
				MarketTextAnalyzer.topTechnologies(textFields, TOP_LIMIT),
				toRankedList(locationRows, TOP_LIMIT));
	}

	private static SalaryInsights buildSalaryInsights(long totalJobs, List<Object[]> salaryRows) {
		if (salaryRows.isEmpty()) {
			return SalaryInsights.empty(totalJobs);
		}

		List<BigDecimal> midpoints = new ArrayList<>();
		BigDecimal min = null;
		BigDecimal max = null;
		Map<String, List<BigDecimal>> byWorkMode = new LinkedHashMap<>();

		for (Object[] row : salaryRows) {
			String workMode = (String) row[0];
			BigDecimal salaryMin = (BigDecimal) row[1];
			BigDecimal salaryMax = (BigDecimal) row[2];
			BigDecimal midpoint = midpoint(salaryMin, salaryMax);
			if (midpoint == null) {
				continue;
			}

			midpoints.add(midpoint);
			min = min == null ? effectiveMin(salaryMin, salaryMax, midpoint) : min.min(effectiveMin(salaryMin, salaryMax, midpoint));
			max = max == null ? effectiveMax(salaryMin, salaryMax, midpoint) : max.max(effectiveMax(salaryMin, salaryMax, midpoint));

			String label = formatWorkMode(workMode);
			byWorkMode.computeIfAbsent(label, ignored -> new ArrayList<>()).add(midpoint);
		}

		if (midpoints.isEmpty()) {
			return SalaryInsights.empty(totalJobs);
		}

		midpoints.sort(Comparator.naturalOrder());
		BigDecimal average = midpoints.stream()
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(midpoints.size()), 0, RoundingMode.HALF_UP);
		BigDecimal median = midpoints.get(midpoints.size() / 2);

		Map<String, BigDecimal> averageByWorkMode = byWorkMode.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> average(entry.getValue()),
						(left, right) -> left,
						LinkedHashMap::new));

		return new SalaryInsights(
				midpoints.size(),
				totalJobs - midpoints.size(),
				average,
				median,
				min,
				max,
				averageByWorkMode);
	}

	private static BigDecimal midpoint(BigDecimal salaryMin, BigDecimal salaryMax) {
		if (salaryMin != null && salaryMax != null) {
			return salaryMin.add(salaryMax).divide(BigDecimal.valueOf(2), 0, RoundingMode.HALF_UP);
		}
		if (salaryMin != null) {
			return salaryMin;
		}
		if (salaryMax != null) {
			return salaryMax;
		}
		return null;
	}

	private static BigDecimal effectiveMin(BigDecimal salaryMin, BigDecimal salaryMax, BigDecimal midpoint) {
		if (salaryMin != null) {
			return salaryMin;
		}
		if (salaryMax != null) {
			return salaryMax;
		}
		return midpoint;
	}

	private static BigDecimal effectiveMax(BigDecimal salaryMin, BigDecimal salaryMax, BigDecimal midpoint) {
		if (salaryMax != null) {
			return salaryMax;
		}
		if (salaryMin != null) {
			return salaryMin;
		}
		return midpoint;
	}

	private static BigDecimal average(List<BigDecimal> values) {
		return values.stream()
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(values.size()), 0, RoundingMode.HALF_UP);
	}

	private static Map<String, Long> labelCountMap(List<Object[]> rows, java.util.function.Function<String, String> labelFn) {
		Map<String, Long> result = new LinkedHashMap<>();
		for (Object[] row : rows) {
			String label = labelFn.apply((String) row[0]);
			result.merge(label, (Long) row[1], Long::sum);
		}
		return sortByCountDesc(result);
	}

	private static List<RankedCount> toRankedList(List<Object[]> rows, int limit) {
		return rows.stream()
				.limit(limit)
				.map(row -> new RankedCount((String) row[0], (Long) row[1]))
				.toList();
	}

	private static Map<String, Long> sortByCountDesc(Map<String, Long> counts) {
		return counts.entrySet().stream()
				.sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue).reversed()
						.thenComparing(Map.Entry::getKey))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(left, right) -> left,
						LinkedHashMap::new));
	}

	static String formatWorkMode(String workMode) {
		if (workMode == null || workMode.isBlank()) {
			return "Sin modalidad";
		}
		return switch (workMode.toLowerCase(Locale.ROOT)) {
			case "remote", "remoto" -> "Remoto";
			case "hybrid", "hibrido", "híbrido" -> "Híbrido";
			case "onsite", "presencial" -> "Presencial";
			default -> workMode.trim();
		};
	}

	static String formatEmploymentType(String employmentType) {
		if (employmentType == null || employmentType.isBlank()) {
			return "Sin especificar";
		}
		String normalized = employmentType.toLowerCase(Locale.ROOT);
		if (normalized.contains("tiempo completo") || normalized.contains("full")) {
			return "Tiempo completo";
		}
		if (normalized.contains("medio tiempo") || normalized.contains("part")) {
			return "Medio tiempo";
		}
		if (normalized.contains("contrato") || normalized.contains("contract")) {
			return "Por contrato";
		}
		if (normalized.contains("temporal") || normalized.contains("temp")) {
			return "Temporal";
		}
		return employmentType.trim();
	}

	private static String normalize(String value, String fallback) {
		if (value == null || value.isBlank()) {
			return fallback;
		}
		return value.trim();
	}
}
