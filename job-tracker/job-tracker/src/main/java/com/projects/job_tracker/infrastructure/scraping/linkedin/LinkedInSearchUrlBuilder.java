package com.projects.job_tracker.infrastructure.scraping.linkedin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.infrastructure.scraping.UrlSlugifier;

public final class LinkedInSearchUrlBuilder {

	private static final String BASE_URL = "https://www.linkedin.com/jobs/search/?";

	private LinkedInSearchUrlBuilder() {
	}

	public static String build(ScrapeCriteria criteria) {
		ScrapeFilterSet filters = criteria.filters();
		StringBuilder url = new StringBuilder(BASE_URL);
		url.append("keywords=").append(encode(criteria.keywords()));

		String location = filters.location();
		if (location != null && !location.isBlank()) {
			url.append("&location=").append(encode(location));
		}

		String workType = mapWorkModeToLinkedIn(filters.workMode());
		if (workType != null) {
			url.append("&f_WT=").append(workType);
		}

		String jobType = mapEmploymentTypeToLinkedIn(filters.employmentType());
		if (jobType != null) {
			url.append("&f_JT=").append(jobType);
		}

		if (filters.postedWithinDays() != null) {
			url.append("&f_TPR=r").append(filters.postedWithinDays() * 86400);
		}
		else {
			url.append("&f_TPR=r604800");
		}

		return url.toString();
	}

	private static String mapWorkModeToLinkedIn(String workMode) {
		if (workMode == null || workMode.isBlank()) {
			return null;
		}
		String slug = UrlSlugifier.slugify(workMode);
		return switch (slug) {
			case "remote", "remoto" -> "2";
			case "hybrid", "hibrido", "mixto" -> "3";
			case "onsite", "presencial", "oficina", "en-oficina" -> "1";
			default -> null;
		};
	}

	private static String mapEmploymentTypeToLinkedIn(String employmentType) {
		if (employmentType == null || employmentType.isBlank()) {
			return null;
		}
		String slug = UrlSlugifier.slugify(employmentType);
		return switch (slug) {
			case "permanente", "tiempo-completo", "full-time" -> "F";
			case "contrato", "contract" -> "C";
			case "medio-tiempo", "part-time" -> "P";
			case "temporal", "temporary" -> "T";
			default -> null;
		};
	}

	private static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
	}
}
