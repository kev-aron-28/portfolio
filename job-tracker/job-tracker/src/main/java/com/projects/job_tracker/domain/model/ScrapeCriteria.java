package com.projects.job_tracker.domain.model;

import java.math.BigDecimal;

public record ScrapeCriteria(String keywords, ScrapeFilterSet filters, int maxResults) {

	public ScrapeCriteria {
		if (keywords == null || keywords.isBlank()) {
			throw new IllegalArgumentException("Keywords are required");
		}
		if (maxResults <= 0) {
			throw new IllegalArgumentException("maxResults must be positive");
		}
		if (filters == null) {
			filters = ScrapeFilterSet.empty();
		}
	}

	public String location() {
		return filters.location();
	}

	public static ScrapeCriteria of(String keywords, String location, int maxResults) {
		return new ScrapeCriteria(keywords, new ScrapeFilterSet(location, null, null, null, null, null), maxResults);
	}
}
