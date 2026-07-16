package com.projects.job_tracker.infrastructure.scraping.indeed;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapedJob;

class IndeedJobParserTest {

	private final IndeedJobParser parser = new IndeedJobParser();

	@Test
	void parsesMosaicSearchResults() throws Exception {
		String html = loadFixture("scraping/indeed/search-results.html");

		var jobs = parser.parse(html, 10);

		assertThat(jobs).hasSize(2);
		ScrapedJob first = jobs.get(0);
		assertThat(first.platform()).isEqualTo(JobPlatform.INDEED);
		assertThat(first.title()).isEqualTo("Desarrollador Fullstack Java/ Angular");
		assertThat(first.companyName()).isEqualTo("VASS Mexico");
		assertThat(first.location()).isEqualTo("México");
		assertThat(first.externalId()).isEqualTo("b9fb3bb7ee6744ab");
		assertThat(first.url()).isEqualTo("https://mx.indeed.com/viewjob?jk=b9fb3bb7ee6744ab");

		ScrapedJob second = jobs.get(1);
		assertThat(second.title()).isEqualTo("Java API Developer - Remoto en México");
		assertThat(second.companyName()).isEqualTo("Capgemini Engineering");
		assertThat(second.location()).isEqualTo("Desde casa");
		assertThat(second.workMode()).isEqualTo("remote");
		assertThat(second.employmentType()).isEqualTo("Tiempo completo");
		assertThat(second.salaryMin()).isNotNull();
	}

	@Test
	void enrichesRightPaneDetail() throws Exception {
		String searchHtml = loadFixture("scraping/indeed/search-results.html");
		String panelHtml = loadFixture("scraping/indeed/detail-panel.html");
		ScrapedJob summary = parser.parse(searchHtml, 10).get(1);

		ScrapedJob enriched = parser.enrichWithDetail(summary, panelHtml).orElseThrow();

		assertThat(enriched.description()).contains("APIs REST");
		assertThat(enriched.title()).isEqualTo("Java API Developer - Remoto en México");
		assertThat(enriched.companyName()).isEqualTo("Capgemini Engineering");
		assertThat(enriched.workMode()).isEqualTo("remote");
	}

	private String loadFixture(String path) throws Exception {
		return StreamUtils.copyToString(
				new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
	}
}
