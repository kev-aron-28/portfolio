package com.projects.job_tracker.infrastructure.scraping.indeed;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;

class IndeedSearchUrlBuilderTest {

	@Test
	void buildsBasicSearchUrl() {
		String url = IndeedSearchUrlBuilder.build(
				ScrapeCriteria.of("java", null, 20));

		assertThat(url).isEqualTo("https://mx.indeed.com/jobs?q=java");
	}

	@Test
	void buildsUrlWithLocationAndFilters() {
		String url = IndeedSearchUrlBuilder.build(new ScrapeCriteria(
				"java developer",
				new ScrapeFilterSet("cdmx", null, null, "permanente", "remote", 7),
				20));

		assertThat(url)
				.startsWith("https://mx.indeed.com/jobs?")
				.contains("q=java")
				.contains("l=cdmx")
				.contains("fromage=7")
				.contains("remotejob=1")
				.contains("jt=fulltime");
	}
}
