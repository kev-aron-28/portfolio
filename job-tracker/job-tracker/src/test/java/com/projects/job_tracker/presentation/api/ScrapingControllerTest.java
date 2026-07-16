package com.projects.job_tracker.presentation.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;

@WebMvcTest(ScrapingController.class)
class ScrapingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ScrapeJobsUseCase scrapeJobsUseCase;

	@Test
	void runsScrapingWithKeywords() throws Exception {
		when(scrapeJobsUseCase.execute(any()))
				.thenReturn(new ScrapeJobsUseCase.ScrapeResult(1, 1, 0, List.of(), List.of()));

		String payload = """
				{
				  "keywords": "qa engineer",
				  "location": "monterrey",
				  "platforms": ["occ"],
				  "maxResults": 5
				}
				""";

		mockMvc.perform(post("/scraping/run").contentType(MediaType.APPLICATION_JSON).content(payload))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.scraped").value(1))
				.andExpect(jsonPath("$.imported").value(1))
				.andExpect(jsonPath("$.duplicates").value(0));
	}
}
