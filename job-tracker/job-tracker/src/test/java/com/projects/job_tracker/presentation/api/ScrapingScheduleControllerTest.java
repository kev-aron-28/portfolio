package com.projects.job_tracker.presentation.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.job_tracker.application.automation.CreateScrapingScheduleUseCase;
import com.projects.job_tracker.application.automation.ListScrapingSchedulesUseCase;
import com.projects.job_tracker.application.automation.RunScheduledScrapingUseCase;
import com.projects.job_tracker.application.automation.UpdateScrapingScheduleUseCase;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSchedule;

@WebMvcTest(ScrapingScheduleController.class)
class ScrapingScheduleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CreateScrapingScheduleUseCase createScrapingScheduleUseCase;

	@MockitoBean
	private ListScrapingSchedulesUseCase listScrapingSchedulesUseCase;

	@MockitoBean
	private UpdateScrapingScheduleUseCase updateScrapingScheduleUseCase;

	@MockitoBean
	private RunScheduledScrapingUseCase runScheduledScrapingUseCase;

	@Test
	void listsSchedules() throws Exception {
		when(listScrapingSchedulesUseCase.execute())
				.thenReturn(List.of(new ScrapingSchedule(
						1L,
						2L,
						List.of(JobPlatform.OCC),
						60,
						20,
						true,
						null,
						Instant.parse("2026-07-06T12:00:00Z"))));

		mockMvc.perform(get("/api/schedules"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].profileId").value(2))
				.andExpect(jsonPath("$[0].platforms[0]").value("occ"));
	}

	@Test
	void createsSchedule() throws Exception {
		when(createScrapingScheduleUseCase.execute(any()))
				.thenReturn(new ScrapingSchedule(
						5L,
						2L,
						List.of(JobPlatform.OCC),
						30,
						10,
						true,
						null,
						Instant.now()));

		String payload = """
				{
				  "profileId": 2,
				  "platforms": ["occ"],
				  "intervalMinutes": 30,
				  "maxResults": 10
				}
				""";

		mockMvc.perform(post("/api/schedules").contentType(MediaType.APPLICATION_JSON).content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(5));
	}

	@Test
	void disablesSchedule() throws Exception {
		when(updateScrapingScheduleUseCase.execute(any(), any()))
				.thenReturn(new ScrapingSchedule(
						5L,
						2L,
						List.of(JobPlatform.OCC),
						30,
						10,
						false,
						Instant.now(),
						Instant.now()));

		mockMvc.perform(patch("/api/schedules/5")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"enabled\": false}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.enabled").value(false));
	}
}
