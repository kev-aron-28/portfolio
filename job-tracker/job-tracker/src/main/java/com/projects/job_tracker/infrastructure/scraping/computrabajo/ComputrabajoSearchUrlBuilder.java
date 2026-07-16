package com.projects.job_tracker.infrastructure.scraping.computrabajo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.infrastructure.scraping.UrlSlugifier;

public final class ComputrabajoSearchUrlBuilder {

	private static final String BASE_URL = "https://mx.computrabajo.com/trabajo-de-";

	private ComputrabajoSearchUrlBuilder() {
	}

	public static String build(ScrapeCriteria criteria) {
		ScrapeFilterSet filters = criteria.filters();
		String keywordSlug = UrlSlugifier.slugify(criteria.keywords());
		if (keywordSlug.isEmpty()) {
			throw new IllegalArgumentException("Keywords cannot be slugified to an empty value");
		}

		StringBuilder url = new StringBuilder(BASE_URL).append(keywordSlug);

		String workModeSegment = mapWorkModeSegment(filters.workMode());
		if (workModeSegment != null) {
			url.append(workModeSegment);
		}
		else {
			String locationSlug = UrlSlugifier.slugify(filters.location());
			if (!locationSlug.isEmpty()) {
				url.append("-en-").append(locationSlug);
			}
		}

		String employmentSegment = mapEmploymentSegment(filters.employmentType());
		if (employmentSegment != null) {
			url.append(employmentSegment);
		}

		List<String> query = new ArrayList<>();
		Integer pubdate = mapPostedWithinDays(filters.postedWithinDays());
		if (pubdate != null) {
			query.add("pubdate=" + pubdate);
		}
		Integer salaryFilter = mapSalaryFilter(filters.salaryMin());
		if (salaryFilter != null) {
			query.add("sal=" + salaryFilter);
		}
		if (!query.isEmpty()) {
			url.append('?').append(String.join("&", query));
		}
		return url.toString();
	}

	private static String mapWorkModeSegment(String workMode) {
		if (workMode == null || workMode.isBlank()) {
			return null;
		}
		String slug = UrlSlugifier.slugify(workMode);
		return switch (slug) {
			case "remote", "remoto" -> "-en-remoto";
			case "hybrid", "hibrido", "mixto" -> "-hibrido";
			default -> null;
		};
	}

	private static String mapEmploymentSegment(String employmentType) {
		if (employmentType == null || employmentType.isBlank()) {
			return null;
		}
		String slug = UrlSlugifier.slugify(employmentType);
		return switch (slug) {
			case "permanente", "tiempo-completo", "full-time", "fulltime" -> "-jornada-tiempo-completo";
			case "medio-tiempo", "part-time", "parttime" -> "-jornada-medio-tiempo";
			case "beca", "practicas", "beca-practicas" -> "-jornada-beca-practicas";
			case "por-horas" -> "-jornada-por-horas";
			default -> null;
		};
	}

	private static Integer mapPostedWithinDays(Integer postedWithinDays) {
		if (postedWithinDays == null || postedWithinDays <= 0) {
			return null;
		}
		if (postedWithinDays <= 1) {
			return 1;
		}
		if (postedWithinDays <= 3) {
			return 3;
		}
		if (postedWithinDays <= 7) {
			return 7;
		}
		if (postedWithinDays <= 15) {
			return 15;
		}
		return 30;
	}

	private static Integer mapSalaryFilter(BigDecimal salaryMin) {
		if (salaryMin == null) {
			return null;
		}
		int value = salaryMin.intValue();
		if (value >= 80000) {
			return 10;
		}
		if (value >= 65000) {
			return 9;
		}
		if (value >= 50000) {
			return 8;
		}
		if (value >= 40000) {
			return 7;
		}
		if (value >= 30000) {
			return 6;
		}
		if (value >= 20000) {
			return 5;
		}
		if (value >= 15000) {
			return 4;
		}
		if (value >= 10000) {
			return 3;
		}
		if (value >= 5000) {
			return 2;
		}
		return 1;
	}
}
