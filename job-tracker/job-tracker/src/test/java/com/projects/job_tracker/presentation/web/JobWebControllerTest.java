package com.projects.job_tracker.presentation.web;

import static com.projects.job_tracker.testutil.TestJobs.job;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.analytics.GetJobDetailUseCase;
import com.projects.job_tracker.application.analytics.ListJobListingsUseCase;
import com.projects.job_tracker.application.application.CreateApplicationUseCase;
import com.projects.job_tracker.domain.model.JobDetail;

import static com.projects.job_tracker.testutil.TestJobs.listing;

@WebMvcTest(JobWebController.class)
class JobWebControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ListJobListingsUseCase listJobListingsUseCase;

	@MockitoBean
	private GetJobDetailUseCase getJobDetailUseCase;

	@MockitoBean
	private CreateApplicationUseCase createApplicationUseCase;

	@Test
	void rendersJobList() throws Exception {
		when(listJobListingsUseCase.execute(any())).thenReturn(List.of());

		mockMvc.perform(get("/jobs"))
				.andExpect(status().isOk())
				.andExpect(view().name("jobs/list"));
	}

	@Test
	void rendersGroupedJobListWithCards() throws Exception {
		when(listJobListingsUseCase.execute(any())).thenReturn(List.of(
				listing(1L, "Java Dev", "Acme", "CDMX", "linkedin", BigDecimal.valueOf(30000), BigDecimal.valueOf(50000),
						Instant.parse("2026-01-01T00:00:00Z"), "https://example.com/1", null),
				listing(2L, "Backend Dev", "Beta", "GDL", "occ", null, null,
						Instant.parse("2026-01-02T00:00:00Z"), "https://example.com/2", null)));

		mockMvc.perform(get("/jobs"))
				.andExpect(status().isOk())
				.andExpect(view().name("jobs/list"));
	}

	@Test
	void rendersJobDetail() throws Exception {
		var job = job(
				1L,
				"Java Dev",
				2L,
				"Description",
				"CDMX",
				null,
				null,
				"occ",
				"https://example.com/job",
				Instant.now());
		when(getJobDetailUseCase.execute(1L)).thenReturn(new JobDetail(job, "Acme", "https://acme.com", Optional.empty()));

		mockMvc.perform(get("/jobs/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("jobs/detail"));
	}
}
