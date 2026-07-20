package com.projects.job_tracker.presentation.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.automation.CreateScrapingScheduleUseCase;
import com.projects.job_tracker.application.automation.ListScrapingSchedulesUseCase;
import com.projects.job_tracker.application.automation.RunScheduledScrapingUseCase;
import com.projects.job_tracker.application.automation.UpdateScrapingScheduleUseCase;
import com.projects.job_tracker.application.profile.CreateSearchProfileUseCase;
import com.projects.job_tracker.application.profile.ListSearchProfilesUseCase;
import com.projects.job_tracker.application.scraping.GetScrapingSettingsUseCase;
import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.application.scraping.UpdateScrapingSettingsUseCase;
import com.projects.job_tracker.application.segment.ListMarketSegmentsUseCase;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSettings;

@WebMvcTest(ScrapingWebController.class)
class ScrapingWebControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ScrapeJobsUseCase scrapeJobsUseCase;

	@MockitoBean
	private GetScrapingSettingsUseCase getScrapingSettingsUseCase;

	@MockitoBean
	private UpdateScrapingSettingsUseCase updateScrapingSettingsUseCase;

	@MockitoBean
	private ListSearchProfilesUseCase listSearchProfilesUseCase;

	@MockitoBean
	private CreateSearchProfileUseCase createSearchProfileUseCase;

	@MockitoBean
	private ListScrapingSchedulesUseCase listScrapingSchedulesUseCase;

	@MockitoBean
	private CreateScrapingScheduleUseCase createScrapingScheduleUseCase;

	@MockitoBean
	private UpdateScrapingScheduleUseCase updateScrapingScheduleUseCase;

	@MockitoBean
	private RunScheduledScrapingUseCase runScheduledScrapingUseCase;

	@MockitoBean
	private ListMarketSegmentsUseCase listMarketSegmentsUseCase;

	@Test
	void rendersScrapingPage() throws Exception {
		when(getScrapingSettingsUseCase.execute())
				.thenReturn(new ScrapingSettings(1000, 20, List.of(JobPlatform.OCC, JobPlatform.LINKEDIN), null, 30000, null, 30000, null, 30000, null, 30000, true, 60000));
		when(listSearchProfilesUseCase.execute()).thenReturn(List.of());
		when(listMarketSegmentsUseCase.execute()).thenReturn(List.of());
		when(listScrapingSchedulesUseCase.execute()).thenReturn(List.of());

		mockMvc.perform(get("/scraping"))
				.andExpect(status().isOk())
				.andExpect(view().name("scraping/index"));
	}

	@Test
	void runsScrapingFromForm() throws Exception {
		when(scrapeJobsUseCase.execute(any()))
				.thenReturn(new ScrapeJobsUseCase.ScrapeResult(5, 3, 2, List.of(), List.of()));

		mockMvc.perform(post("/scraping/run")
						.contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
						.param("keywords", "java")
						.param("platforms", "occ")
						.param("maxResults", "10")
						.param("segmentId", "3"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/scraping?tab=run"))
				.andExpect(flash().attributeExists("successMessage"));
	}
}
