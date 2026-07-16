package com.projects.job_tracker.infrastructure.scraping.linkedin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.projects.job_tracker.domain.model.JobPlatform;

class LinkedInJobParserTest {

	private final LinkedInJobParser parser = new LinkedInJobParser();

	@Test
	void parsesSearchResultsFromSavedHtml() throws IOException {
		String html = new ClassPathResource("scraping/linkedin/search-results.html")
				.getContentAsString(StandardCharsets.UTF_8);

		var jobs = parser.parse(html, 10);

		assertThat(jobs).hasSize(2);
		assertThat(jobs.get(0).platform()).isEqualTo(JobPlatform.LINKEDIN);
		assertThat(jobs.get(0).title()).isEqualTo("Java Developer");
		assertThat(jobs.get(0).companyName()).isEqualTo("TechCorp");
		assertThat(jobs.get(0).location()).isEqualTo("Mexico City, Mexico");
		assertThat(jobs.get(0).url()).isEqualTo("https://www.linkedin.com/jobs/view/445566");
		assertThat(jobs.get(0).externalId()).isEqualTo("445566");
	}
}
