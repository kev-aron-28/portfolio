package com.projects.job_tracker.infrastructure.scraping.computrabajo;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapedJob;

class ComputrabajoJobParserTest {

	private final ComputrabajoJobParser parser = new ComputrabajoJobParser();

	@Test
	void parsesSearchResults() throws Exception {
		String html = loadFixture("scraping/computrabajo/search-results.html");

		var jobs = parser.parse(html, 10);

		assertThat(jobs).hasSize(2);
		ScrapedJob first = jobs.get(0);
		assertThat(first.platform()).isEqualTo(JobPlatform.COMPUTRABAJO);
		assertThat(first.title()).isEqualTo("Desarrollador Fullstack (Angular/Java)");
		assertThat(first.companyName()).isEqualTo("IDS Comercial, S.A. de C.V.");
		assertThat(first.location()).isEqualTo("Álvaro Obregón, Ciudad de México DF");
		assertThat(first.workMode()).isEqualTo("hybrid");
		assertThat(first.externalId()).isEqualTo("88B36D30CD3743BA61373E686DCF3405");
		assertThat(first.url()).doesNotContain("#");

		ScrapedJob second = jobs.get(1);
		assertThat(second.title()).isEqualTo("Desarrollador Java");
		assertThat(second.companyName()).isEqualTo("CORUS");
		assertThat(second.salaryMin()).isNotNull();
	}

	@Test
	void enrichesDetailPanel() throws Exception {
		String searchHtml = loadFixture("scraping/computrabajo/search-results.html");
		String panelHtml = loadFixture("scraping/computrabajo/detail-panel.html");
		ScrapedJob summary = parser.parse(searchHtml, 10).get(1);

		ScrapedJob enriched = parser.enrichWithDetail(summary, panelHtml).orElseThrow();

		assertThat(enriched.description()).contains("Spring Boot");
		assertThat(enriched.employmentType()).isEqualTo("Tiempo Completo");
		assertThat(enriched.requirements()).contains("Universitario");
		assertThat(enriched.workMode()).isEqualTo("hybrid");
	}

	private String loadFixture(String path) throws Exception {
		return StreamUtils.copyToString(
				new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
	}
}
