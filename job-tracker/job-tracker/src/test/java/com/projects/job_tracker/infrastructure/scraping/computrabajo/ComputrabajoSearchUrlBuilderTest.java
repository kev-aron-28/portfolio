package com.projects.job_tracker.infrastructure.scraping.computrabajo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;

class ComputrabajoSearchUrlBuilderTest {

	@Test
	void buildsBasicKeywordUrl() {
		String url = ComputrabajoSearchUrlBuilder.build(ScrapeCriteria.of("java", null, 20));

		assertThat(url).isEqualTo("https://mx.computrabajo.com/trabajo-de-java");
	}

	@Test
	void buildsUrlWithLocationRemoteAndFilters() {
		String url = ComputrabajoSearchUrlBuilder.build(new ScrapeCriteria(
				"java developer",
				new ScrapeFilterSet("jalisco", null, null, "permanente", "remote", 7),
				20));

		assertThat(url).startsWith("https://mx.computrabajo.com/trabajo-de-java-developer-en-remoto-jornada-tiempo-completo");
		assertThat(url).contains("pubdate=7");
	}

	@Test
	void mapsSalaryMinToSalQueryParam() {
		String url = ComputrabajoSearchUrlBuilder.build(new ScrapeCriteria(
				"java",
				new ScrapeFilterSet(null, java.math.BigDecimal.valueOf(30000), null, null, null, null),
				20));

		assertThat(url).isEqualTo("https://mx.computrabajo.com/trabajo-de-java?sal=6");
	}
}
