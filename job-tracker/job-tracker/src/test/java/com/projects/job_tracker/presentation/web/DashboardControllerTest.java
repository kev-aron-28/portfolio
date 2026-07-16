package com.projects.job_tracker.presentation.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.MarketInsights;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GetDashboardMetricsUseCase getDashboardMetricsUseCase;

	@MockitoBean
	private ListJobListingsUseCase listJobListingsUseCase;

	@Test
	void rendersDashboard() throws Exception {
		when(getDashboardMetricsUseCase.execute())
				.thenReturn(new DashboardMetrics(0, 0, 0, 0, Map.of(), new EnumMap<>(com.projects.job_tracker.domain.model.ApplicationStatus.class), MarketInsights.empty(0)));
		when(listJobListingsUseCase.execute(ListJobListingsUseCase.JobListingQuery.empty())).thenReturn(List.of());

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard"));
	}
}
