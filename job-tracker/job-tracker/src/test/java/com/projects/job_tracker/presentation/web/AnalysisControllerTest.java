package com.projects.job_tracker.presentation.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.analytics.AnalyzeSearchReportUseCase;
import com.projects.job_tracker.domain.model.ReportFinding;
import com.projects.job_tracker.domain.model.ReportMetric;
import com.projects.job_tracker.domain.model.ReportShare;
import com.projects.job_tracker.domain.model.SearchReport;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AnalyzeSearchReportUseCase analyzeSearchReportUseCase;

	@Test
	void rendersAnalysisReport() throws Exception {
		when(analyzeSearchReportUseCase.execute()).thenReturn(new SearchReport(
				"Has postulado a 10 de 20 vacantes (50%).",
				List.of(new ReportFinding("El pipeline aún no avanza", "Todas siguen en Postulado.", "info")),
				List.of(new ReportMetric("Cobertura", "50%", "10 de 20 vacantes")),
				List.of(new ReportShare("Postulado", 10, 100)),
				List.of(),
				List.of(new ReportShare("14 sep", 3, 100)),
				List.of(new ReportShare("occ", 8, 80)),
				List.of(new ReportShare("Remoto", 6, 60)),
				List.of(new ReportShare("CDMX", 7, 70)),
				true));

		mockMvc.perform(get("/analisis"))
				.andExpect(status().isOk())
				.andExpect(view().name("analysis"))
				.andExpect(content().string(containsString("Análisis")))
				.andExpect(content().string(containsString("Hallazgos")))
				.andExpect(content().string(containsString("Tiempo en el estado actual")))
				.andExpect(content().string(containsString("Ritmo de postulaciones")))
				.andExpect(content().string(containsString("Has postulado a 10 de 20 vacantes (50%).")));
	}
}
