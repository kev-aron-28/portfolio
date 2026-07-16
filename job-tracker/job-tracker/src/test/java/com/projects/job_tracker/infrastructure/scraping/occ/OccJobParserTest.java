package com.projects.job_tracker.infrastructure.scraping.occ;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapedJob;

class OccJobParserTest {

	private final OccJobParser parser = new OccJobParser();

	@Test
	void parsesSearchResultsFromCardJobOffer() throws IOException {
		String html = new ClassPathResource("scraping/occ/search-results.html")
				.getContentAsString(StandardCharsets.UTF_8);

		var jobs = parser.parse(html, 10);

		assertThat(jobs).hasSize(2);
		assertThat(jobs.get(0).platform()).isEqualTo(JobPlatform.OCC);
		assertThat(jobs.get(0).title()).isEqualTo("Desarrollador Senior Backend (Java) - Hibrido CDMX");
		assertThat(jobs.get(0).url()).isEqualTo("https://www.occ.com.mx/empleo/oferta/21239558");
		assertThat(jobs.get(0).externalId()).isEqualTo("21239558");
	}

	@Test
	void parsesDetailPanelHtml() throws IOException {
		String html = new ClassPathResource("scraping/occ/detail-panel.html")
				.getContentAsString(StandardCharsets.UTF_8);

		ScrapedJob job = parser.parseDetailPanel(html).orElseThrow();

		assertThat(job.title()).isEqualTo("Desarrollador Senior Backend (Java) - Hibrido CDMX");
		assertThat(job.companyName()).isEqualTo("CORUS E3 CONSULTING SERVICES");
		assertThat(job.location()).isEqualTo("Ciudad de México");
		assertThat(job.salaryMin()).isEqualByComparingTo("47400");
		assertThat(job.salaryMax()).isEqualByComparingTo("47400");
		assertThat(job.url()).isEqualTo("https://www.occ.com.mx/empleo/oferta/21239558");
		assertThat(job.externalId()).isEqualTo("21239558");
	}

	@Test
	void respectsMaxResults() throws IOException {
		String html = new ClassPathResource("scraping/occ/search-results.html")
				.getContentAsString(StandardCharsets.UTF_8);

		var jobs = parser.parse(html, 1);

		assertThat(jobs).hasSize(1);
		assertThat(jobs.getFirst().title()).isEqualTo("Desarrollador Senior Backend (Java) - Hibrido CDMX");
	}
}
