package com.projects.job_tracker.presentation.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.analytics.GetDashboardMetricsUseCase;
import com.projects.job_tracker.application.analytics.ListJobListingsUseCase;
import com.projects.job_tracker.application.profile.ListSearchProfilesUseCase;
import com.projects.job_tracker.application.scraping.GetScrapingSettingsUseCase;
import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.application.segment.CreateMarketSegmentUseCase;
import com.projects.job_tracker.application.segment.DeleteMarketSegmentUseCase;
import com.projects.job_tracker.application.segment.GetMarketSegmentUseCase;
import com.projects.job_tracker.application.segment.ListMarketSegmentsUseCase;
import com.projects.job_tracker.application.segment.UpdateMarketSegmentUseCase;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.MarketInsights;
import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@WebMvcTest(MarketSegmentWebController.class)
class MarketSegmentWebControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ListMarketSegmentsUseCase listMarketSegmentsUseCase;

	@MockitoBean
	private GetMarketSegmentUseCase getMarketSegmentUseCase;

	@MockitoBean
	private CreateMarketSegmentUseCase createMarketSegmentUseCase;

	@MockitoBean
	private UpdateMarketSegmentUseCase updateMarketSegmentUseCase;

	@MockitoBean
	private DeleteMarketSegmentUseCase deleteMarketSegmentUseCase;

	@MockitoBean
	private GetDashboardMetricsUseCase getDashboardMetricsUseCase;

	@MockitoBean
	private ListJobListingsUseCase listJobListingsUseCase;

	@MockitoBean
	private ListSearchProfilesUseCase listSearchProfilesUseCase;

	@MockitoBean
	private ScrapeJobsUseCase scrapeJobsUseCase;

	@MockitoBean
	private GetScrapingSettingsUseCase getScrapingSettingsUseCase;

	@MockitoBean
	private MarketSegmentRepository marketSegmentRepository;

	private static MarketSegment segment() {
		return new MarketSegment(1L, "Backend", "desc", "java", null, Instant.now());
	}

	private static DashboardMetrics emptyMetrics() {
		return new DashboardMetrics(
				0, 0, 0, 0, Map.of(), new EnumMap<>(ApplicationStatus.class), MarketInsights.empty(0));
	}

	private static ScrapingSettings settings() {
		return new ScrapingSettings(
				1000, 20, List.of(JobPlatform.OCC), null, 30000, null, 30000, null, 30000, null, 30000, true, 60000);
	}

	@Test
	void listRendersSegments() throws Exception {
		when(listMarketSegmentsUseCase.execute()).thenReturn(List.of(segment()));
		when(marketSegmentRepository.countJobs(1L)).thenReturn(3L);

		mockMvc.perform(get("/segments"))
				.andExpect(status().isOk())
				.andExpect(view().name("segments/list"))
				.andExpect(model().attributeExists("segments"));
	}

	@Test
	void detailRendersSegmentMetrics() throws Exception {
		when(getMarketSegmentUseCase.execute(1L)).thenReturn(segment());
		when(getDashboardMetricsUseCase.execute(1L)).thenReturn(emptyMetrics());
		when(listJobListingsUseCase.execute(any())).thenReturn(List.of());
		when(listSearchProfilesUseCase.execute()).thenReturn(List.of());
		when(getScrapingSettingsUseCase.execute()).thenReturn(settings());
		when(marketSegmentRepository.countJobs(1L)).thenReturn(0L);

		mockMvc.perform(get("/segments/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("segments/detail"))
				.andExpect(model().attributeExists("segment", "metrics"));
	}

	@Test
	void scrapeAttributesToSegmentAndRedirects() throws Exception {
		when(getScrapingSettingsUseCase.execute()).thenReturn(settings());
		when(scrapeJobsUseCase.execute(any())).thenReturn(
				new ScrapeJobsUseCase.ScrapeResult(2, 1, 1, List.of(), List.of()));

		mockMvc.perform(post("/segments/1/scrape")
						.contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
						.param("keywords", "java"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("/segments/1*"));
	}
}
