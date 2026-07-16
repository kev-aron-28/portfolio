package com.projects.job_tracker.infrastructure.scraping.occ;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;

class OccSearchUrlBuilderTest {

	@Test
	void buildsKeywordAndLocationUrl() {
		var criteria = new ScrapeCriteria(
				"Java Developer",
				new ScrapeFilterSet("Ciudad de Mexico", null, null, null, null, null),
				10);

		String url = OccSearchUrlBuilder.build(criteria);

		assertThat(url).isEqualTo("https://www.occ.com.mx/empleos/de-java-developer-en-ciudad-de-mexico/");
	}

	@Test
	void buildsOccStyleSlugUrl() {
		var criteria = ScrapeCriteria.of("java", "cdmx", 10);

		String url = OccSearchUrlBuilder.build(criteria);

		assertThat(url).isEqualTo("https://www.occ.com.mx/empleos/de-java-en-cdmx/");
	}

	@Test
	void buildsUrlWithGranularFilters() {
		var criteria = new ScrapeCriteria(
				"java",
				new ScrapeFilterSet("cdmx", new java.math.BigDecimal("20000"), new java.math.BigDecimal("30000"), "permanente", "onsite", 0),
				10);

		String url = OccSearchUrlBuilder.build(criteria);

		assertThat(url).isEqualTo(
				"https://www.occ.com.mx/empleos/de-java-en-cdmx/permanente/tipo-en-oficina/?tm=0&smin=20000&smax=30000");
	}
}
