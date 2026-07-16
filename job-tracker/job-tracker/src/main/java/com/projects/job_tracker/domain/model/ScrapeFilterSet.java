package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;

/**
 * Filtros de búsqueda independientes de plataforma.
 * Cada scraper los traduce a parámetros/segmentos de URL propios.
 */
public record ScrapeFilterSet(
		String location,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String employmentType,
		String workMode,
		Integer postedWithinDays) {

	public static ScrapeFilterSet empty() {
		return new ScrapeFilterSet(null, null, null, null, null, null);
	}

	public boolean isEmpty() {
		return (location == null || location.isBlank())
				&& salaryMin == null
				&& salaryMax == null
				&& (employmentType == null || employmentType.isBlank())
				&& (workMode == null || workMode.isBlank())
				&& postedWithinDays == null;
	}
}
