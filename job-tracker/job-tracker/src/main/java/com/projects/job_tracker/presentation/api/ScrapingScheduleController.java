package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.job_tracker.application.automation.CreateScrapingScheduleUseCase;
import com.projects.job_tracker.application.automation.ListScrapingSchedulesUseCase;
import com.projects.job_tracker.application.automation.RunScheduledScrapingUseCase;
import com.projects.job_tracker.application.automation.UpdateScrapingScheduleUseCase;
import com.projects.job_tracker.presentation.api.dto.CreateScrapingScheduleRequest;
import com.projects.job_tracker.presentation.api.dto.ScheduleRunResponse;
import com.projects.job_tracker.presentation.api.dto.ScrapingScheduleResponse;
import com.projects.job_tracker.presentation.api.dto.UpdateScrapingScheduleRequest;

@RestController
@RequestMapping("/schedules")
public class ScrapingScheduleController {

	private final CreateScrapingScheduleUseCase createScrapingScheduleUseCase;
	private final ListScrapingSchedulesUseCase listScrapingSchedulesUseCase;
	private final UpdateScrapingScheduleUseCase updateScrapingScheduleUseCase;
	private final RunScheduledScrapingUseCase runScheduledScrapingUseCase;

	public ScrapingScheduleController(
			CreateScrapingScheduleUseCase createScrapingScheduleUseCase,
			ListScrapingSchedulesUseCase listScrapingSchedulesUseCase,
			UpdateScrapingScheduleUseCase updateScrapingScheduleUseCase,
			RunScheduledScrapingUseCase runScheduledScrapingUseCase) {
		this.createScrapingScheduleUseCase = createScrapingScheduleUseCase;
		this.listScrapingSchedulesUseCase = listScrapingSchedulesUseCase;
		this.updateScrapingScheduleUseCase = updateScrapingScheduleUseCase;
		this.runScheduledScrapingUseCase = runScheduledScrapingUseCase;
	}

	@GetMapping
	public List<ScrapingScheduleResponse> listSchedules() {
		return listScrapingSchedulesUseCase.execute().stream()
				.map(ScrapingScheduleResponse::from)
				.toList();
	}

	@PostMapping
	public ResponseEntity<ScrapingScheduleResponse> createSchedule(@RequestBody CreateScrapingScheduleRequest request) {
		ScrapingScheduleResponse response =
				ScrapingScheduleResponse.from(createScrapingScheduleUseCase.execute(request.toCommand()));
		return ResponseEntity.created(URI.create("/schedules/" + response.id())).body(response);
	}

	@PatchMapping("/{id}")
	public ScrapingScheduleResponse updateSchedule(
			@PathVariable Long id, @RequestBody UpdateScrapingScheduleRequest request) {
		var command = new UpdateScrapingScheduleUseCase.UpdateScrapingScheduleCommand(request.enabled());
		return ScrapingScheduleResponse.from(updateScrapingScheduleUseCase.execute(id, command));
	}

	@PostMapping("/{id}/run")
	public ScheduleRunResponse runSchedule(@PathVariable Long id) {
		return ScheduleRunResponse.from(runScheduledScrapingUseCase.executeById(id));
	}

	@PostMapping("/run-due")
	public List<ScheduleRunResponse> runDueSchedules() {
		return runScheduledScrapingUseCase.executeAllDue().results().stream()
				.map(ScheduleRunResponse::from)
				.toList();
	}
}
