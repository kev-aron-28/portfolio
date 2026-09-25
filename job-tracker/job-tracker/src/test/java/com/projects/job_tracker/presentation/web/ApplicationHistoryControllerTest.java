package com.projects.job_tracker.presentation.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.analytics.GetApplicationHistoryUseCase;
import com.projects.job_tracker.domain.model.ApplicationHistory;
import com.projects.job_tracker.domain.model.ApplicationHistoryDay;
import com.projects.job_tracker.domain.model.ApplicationHistoryItem;
import com.projects.job_tracker.domain.model.ApplicationHistoryMonth;
import com.projects.job_tracker.domain.model.ApplicationStatus;

@WebMvcTest(ApplicationHistoryController.class)
class ApplicationHistoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GetApplicationHistoryUseCase getApplicationHistoryUseCase;

	@Test
	void rendersApplicationTimeline() throws Exception {
		ApplicationHistoryItem item = new ApplicationHistoryItem(
				8L,
				"Backend Java",
				"Nubank",
				"occ",
				ApplicationStatus.APPLIED,
				LocalDate.of(2026, 9, 20),
				Instant.parse("2026-09-20T18:00:00Z"));
		when(getApplicationHistoryUseCase.execute()).thenReturn(new ApplicationHistory(
				1,
				List.of(new ApplicationHistoryMonth(
						YearMonth.of(2026, 9),
						"Septiembre 2026",
						1,
						List.of(new ApplicationHistoryDay(LocalDate.of(2026, 9, 20), "20 sept", List.of(item)))))));

		mockMvc.perform(get("/historial"))
				.andExpect(status().isOk())
				.andExpect(view().name("history"))
				.andExpect(content().string(containsString("Historial")))
				.andExpect(content().string(containsString("Línea de tiempo")))
				.andExpect(content().string(containsString("Nubank")))
				.andExpect(content().string(containsString("Backend Java")));
	}
}
