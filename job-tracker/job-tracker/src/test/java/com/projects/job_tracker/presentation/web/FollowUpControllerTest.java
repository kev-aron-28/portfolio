package com.projects.job_tracker.presentation.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.analytics.AnalyzeApplicationFollowUpUseCase;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.FollowUpDay;
import com.projects.job_tracker.domain.model.FollowUpEntry;
import com.projects.job_tracker.domain.model.FollowUpOverview;
import com.projects.job_tracker.domain.model.FollowUpSignal;

@WebMvcTest(FollowUpController.class)
class FollowUpControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AnalyzeApplicationFollowUpUseCase analyzeApplicationFollowUpUseCase;

	@Test
	void rendersFollowUpTimeline() throws Exception {
		FollowUpEntry silent = new FollowUpEntry(
				8L,
				"Backend Java",
				"Nubank",
				LocalDate.of(2026, 8, 20),
				Instant.parse("2026-08-20T12:00:00Z"),
				ApplicationStatus.APPLIED,
				30,
				"30 días",
				FollowUpSignal.LIKELY_SILENT,
				"Por el tiempo sin avance, es posible que ya no se comuniquen.");
		when(analyzeApplicationFollowUpUseCase.execute()).thenReturn(new FollowUpOverview(
				1,
				1,
				0,
				0,
				1,
				0,
				List.of(silent),
				List.of(new FollowUpDay(LocalDate.of(2026, 8, 20), "20 ago 2026", 1, 1, List.of(silent)))));

		mockMvc.perform(get("/seguimiento"))
				.andExpect(status().isOk())
				.andExpect(view().name("follow-up"))
				.andExpect(content().string(containsString("Seguimiento")))
				.andExpect(content().string(containsString("Requieren atención")))
				.andExpect(content().string(containsString("Por fecha registrada")))
				.andExpect(content().string(containsString("Backend Java")))
				.andExpect(content().string(containsString("Probable silencio")))
				.andExpect(content().string(containsString("Ver análisis completo")));
	}
}
