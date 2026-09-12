package com.projects.job_tracker.presentation.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import com.projects.job_tracker.domain.model.ApplicationStatus;
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
				.thenReturn(new DashboardMetrics(0, 0, 0, 0, Map.of(), new EnumMap<>(ApplicationStatus.class), MarketInsights.empty(0)));
		when(listJobListingsUseCase.execute(ListJobListingsUseCase.JobListingQuery.empty())).thenReturn(List.of());

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("dashboard"))
				.andExpect(content().string(containsString("Embudo de postulaciones")))
				.andExpect(content().string(containsString("Sin postulaciones registradas.")))
				.andExpect(content().string(containsString("theme-toggle")));
	}

	@Test
	void rendersApplicationFunnelByStatus() throws Exception {
		Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
		byStatus.put(ApplicationStatus.APPLIED, 8L);
		byStatus.put(ApplicationStatus.INTERVIEWING, 2L);
		byStatus.put(ApplicationStatus.REJECTED, 1L);
		when(getDashboardMetricsUseCase.execute())
				.thenReturn(new DashboardMetrics(10, 11, 4, 2, Map.of("occ", 10L), byStatus, MarketInsights.empty(10)));
		when(listJobListingsUseCase.execute(ListJobListingsUseCase.JobListingQuery.empty())).thenReturn(List.of());

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Embudo de postulaciones")))
				.andExpect(content().string(containsString("Postulado")))
				.andExpect(content().string(containsString("Entrevista")))
				.andExpect(content().string(containsString("Rechazado")))
				.andExpect(content().string(containsString("applicationStatus=APPLIED")))
				.andExpect(content().string(containsString("funnel-stage")))
				.andExpect(content().string(containsString("funnel-conversion")))
				.andExpect(content().string(containsString("Salidas del pipeline")));
	}
}
