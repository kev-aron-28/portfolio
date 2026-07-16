package com.projects.job_tracker.presentation.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ScrapeFilterFormatterTest {

	@Test
	void formatsFilterLabels() {
		String json = "{\"location\":\"cdmx\",\"workMode\":\"remote\",\"salaryMin\":20000,\"salaryMax\":50000,"
				+ "\"employmentType\":\"permanente\",\"postedWithinDays\":7}";

		assertThat(ScrapeFilterFormatter.toLabels(json))
				.containsExactly("cdmx", "Remoto", "20000 – 50000", "permanente", "últimos 7 días");
	}

	@Test
	void returnsEmptyForBlankFilters() {
		assertThat(ScrapeFilterFormatter.toLabels((String) null)).isEmpty();
		assertThat(ScrapeFilterFormatter.toLabels("")).isEmpty();
	}
}
