package com.projects.job_tracker.infrastructure.scraping.indeed;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.infrastructure.scraping.UrlSlugifier;

public final class IndeedSearchUrlBuilder {

	private static final String BASE_URL = "https://mx.indeed.com/jobs";

	private IndeedSearchUrlBuilder() {
	}

	public static String build(ScrapeCriteria criteria) {
		ScrapeFilterSet filters = criteria.filters();
		StringBuilder url = new StringBuilder(BASE_URL);
		url.append("?q=").append(encode(criteria.keywords()));

		String location = filters.location();
		if (location != null && !location.isBlank()) {
			url.append("&l=").append(encode(location));
		}

		if (filters.postedWithinDays() != null && filters.postedWithinDays() > 0) {
			url.append("&fromage=").append(filters.postedWithinDays());
		}

		if (isRemote(filters.workMode())) {
			url.append("&remotejob=1");
		}

		String employmentType = mapEmploymentType(filters.employmentType());
		if (employmentType != null) {
			url.append("&jt=").append(employmentType);
		}

		return url.toString();
	}

	private static boolean isRemote(String workMode) {
		if (workMode == null || workMode.isBlank()) {
			return false;
		}
		String slug = UrlSlugifier.slugify(workMode);
		return "remote".equals(slug) || "remoto".equals(slug);
	}

	private static String mapEmploymentType(String employmentType) {
		if (employmentType == null || employmentType.isBlank()) {
			return null;
		}
		String slug = UrlSlugifier.slugify(employmentType);
		return switch (slug) {
			case "permanente", "tiempo-completo", "full-time", "fulltime" -> "fulltime";
			case "medio-tiempo", "part-time", "parttime" -> "parttime";
			case "contrato", "contract" -> "contract";
			case "temporal", "temporary" -> "temporary";
			default -> null;
		};
	}

	private static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
	}
}
