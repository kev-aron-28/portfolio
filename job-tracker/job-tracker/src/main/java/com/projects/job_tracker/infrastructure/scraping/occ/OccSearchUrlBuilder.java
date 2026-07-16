package com.projects.job_tracker.infrastructure.scraping.occ;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.infrastructure.scraping.UrlSlugifier;

public final class OccSearchUrlBuilder {

	private static final String BASE_URL = "https://www.occ.com.mx/empleos/de-";

	private OccSearchUrlBuilder() {
	}

	public static String build(ScrapeCriteria criteria) {
		ScrapeFilterSet filters = criteria.filters();
		String keywordSlug = UrlSlugifier.slugify(criteria.keywords());
		if (keywordSlug.isEmpty()) {
			throw new IllegalArgumentException("Keywords cannot be slugified to an empty value");
		}

		StringBuilder url = new StringBuilder(BASE_URL).append(keywordSlug);
		String locationSlug = UrlSlugifier.slugify(filters.location());
		if (!locationSlug.isEmpty()) {
			url.append("-en-").append(locationSlug);
		}
		url.append('/');

		String employmentSlug = UrlSlugifier.slugify(filters.employmentType());
		if (!employmentSlug.isEmpty()) {
			url.append(employmentSlug).append('/');
		}

		String workModeSlug = mapWorkModeToOccPath(filters.workMode());
		if (workModeSlug != null) {
			url.append("tipo-").append(workModeSlug).append('/');
		}

		List<String> query = new ArrayList<>();
		if (filters.postedWithinDays() != null) {
			query.add("tm=" + filters.postedWithinDays());
		}
		if (filters.salaryMin() != null) {
			query.add("smin=" + filters.salaryMin().intValue());
		}
		if (filters.salaryMax() != null) {
			query.add("smax=" + filters.salaryMax().intValue());
		}
		if (!query.isEmpty()) {
			url.append('?').append(String.join("&", query));
		}
		return url.toString();
	}

	private static String mapWorkModeToOccPath(String workMode) {
		if (workMode == null || workMode.isBlank()) {
			return null;
		}
		String slug = UrlSlugifier.slugify(workMode);
		return switch (slug) {
			case "remote", "remoto", "home-office" -> "home-office";
			case "hybrid", "hibrido", "home-office-mixto", "mixto" -> "home-office-mixto";
			case "onsite", "presencial", "en-oficina", "oficina" -> "en-oficina";
			default -> slug;
		};
	}
}
