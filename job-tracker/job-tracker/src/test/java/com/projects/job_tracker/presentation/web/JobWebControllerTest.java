package com.projects.job_tracker.presentation.web;

import static com.projects.job_tracker.testutil.TestJobs.job;
import static com.projects.job_tracker.testutil.TestJobs.listing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.analytics.GetJobDetailUseCase;
import com.projects.job_tracker.application.analytics.ListJobListingsUseCase;
import com.projects.job_tracker.application.application.CreateApplicationUseCase;
import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.application.job.DeleteJobUseCase;
import com.projects.job_tracker.application.segment.ListMarketSegmentsUseCase;
import com.projects.job_tracker.domain.model.JobDetail;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

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

	@MockitoBean
	private CreateJobUseCase createJobUseCase;

	@MockitoBean
	private DeleteJobUseCase deleteJobUseCase;

	@MockitoBean
	private ListMarketSegmentsUseCase listMarketSegmentsUseCase;

	@MockitoBean
	private MarketSegmentRepository marketSegmentRepository;

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
	void rendersNewJobForm() throws Exception {
		when(listMarketSegmentsUseCase.execute()).thenReturn(List.of());

		mockMvc.perform(get("/jobs/new"))
				.andExpect(status().isOk())
				.andExpect(view().name("jobs/form"));
	}

	@Test
	void createsJobFromFormAndAttachesSegments() throws Exception {
		when(createJobUseCase.execute(any())).thenReturn(job(
				42L,
				"Java Dev",
				2L,
				"Desc",
				"CDMX",
				new BigDecimal("40000"),
				new BigDecimal("60000"),
				"manual",
				"https://example.com/job",
				Instant.now()));

		mockMvc.perform(post("/jobs")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("title", "Java Dev")
						.param("companyName", "Acme")
						.param("companyWebsite", "https://acme.com")
						.param("description", "Desc")
						.param("location", "CDMX")
						.param("salaryMin", "40000")
						.param("salaryMax", "60000")
						.param("source", "manual")
						.param("url", "https://example.com/job")
						.param("workMode", "remote")
						.param("employmentType", "permanente")
						.param("category", "TI")
						.param("requirements", "Java, Spring")
						.param("benefits", "Seguro")
						.param("postedAt", "2026-07-01T10:30")
						.param("segmentIds", "5", "7"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/jobs/42"))
				.andExpect(flash().attributeExists("successMessage"));

		verify(createJobUseCase).execute(any());
		verify(marketSegmentRepository).attachJob(5L, 42L);
		verify(marketSegmentRepository).attachJob(7L, 42L);
	}

	@Test
	void deletesJobAndRedirectsToList() throws Exception {
		mockMvc.perform(post("/jobs/42/delete")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/jobs"))
				.andExpect(flash().attributeExists("successMessage"));

		verify(deleteJobUseCase).execute(42L);
	}

	@Test
	void bulkDeletesSelectedJobs() throws Exception {
		when(deleteJobUseCase.execute(org.mockito.ArgumentMatchers.<java.util.List<Long>>any()))
				.thenReturn(2);

		mockMvc.perform(post("/jobs/bulk-delete")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("jobIds", "1", "2"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/jobs"))
				.andExpect(flash().attributeExists("successMessage"));
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
