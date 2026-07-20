package com.projects.job_tracker.presentation.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.job_tracker.application.application.CreateApplicationUseCase;
import com.projects.job_tracker.application.application.UpdateApplicationUseCase;
import com.projects.job_tracker.presentation.api.dto.ApplicationResponse;
import com.projects.job_tracker.presentation.api.dto.CreateApplicationRequest;
import com.projects.job_tracker.presentation.api.dto.UpdateApplicationRequest;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

	private final CreateApplicationUseCase createApplicationUseCase;
	private final UpdateApplicationUseCase updateApplicationUseCase;

	public ApplicationController(
			CreateApplicationUseCase createApplicationUseCase,
			UpdateApplicationUseCase updateApplicationUseCase) {
		this.createApplicationUseCase = createApplicationUseCase;
		this.updateApplicationUseCase = updateApplicationUseCase;
	}

	@PostMapping
	public ResponseEntity<ApplicationResponse> createApplication(@RequestBody CreateApplicationRequest request) {
		var command = new CreateApplicationUseCase.CreateApplicationCommand(
				request.jobId(),
				request.status(),
				request.appliedAt(),
				request.notes());

		ApplicationResponse response = ApplicationResponse.from(createApplicationUseCase.execute(command));
		return ResponseEntity.created(URI.create("/api/applications/" + response.id())).body(response);
	}

	@PatchMapping("/{id}")
	public ApplicationResponse updateApplication(
			@PathVariable Long id,
			@RequestBody UpdateApplicationRequest request) {
		var command = new UpdateApplicationUseCase.UpdateApplicationCommand(request.status(), request.notes());
		return ApplicationResponse.from(updateApplicationUseCase.execute(id, command));
	}
}
