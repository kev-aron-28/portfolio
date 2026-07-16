package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.application.job.GetJobUseCase;
import com.projects.job_tracker.application.job.ListJobsUseCase;
import com.projects.job_tracker.presentation.api.dto.CreateJobRequest;
import com.projects.job_tracker.presentation.api.dto.JobResponse;

@RestController
@RequestMapping("/jobs")
public class JobController {

	private final CreateJobUseCase createJobUseCase;
	private final GetJobUseCase getJobUseCase;
	private final ListJobsUseCase listJobsUseCase;

	public JobController(
			CreateJobUseCase createJobUseCase,
			GetJobUseCase getJobUseCase,
			ListJobsUseCase listJobsUseCase) {
		this.createJobUseCase = createJobUseCase;
		this.getJobUseCase = getJobUseCase;
		this.listJobsUseCase = listJobsUseCase;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<JobResponse> listJobs() {
		return listJobsUseCase.execute().stream().map(JobResponse::from).toList();
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public JobResponse getJob(@PathVariable Long id) {
		return JobResponse.from(getJobUseCase.execute(id));
	}

	@PostMapping
	public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest request) {
		var command = new CreateJobUseCase.CreateJobCommand(
				request.title(),
				request.companyName(),
				request.companyWebsite(),
				request.description(),
				request.location(),
				request.salaryMin(),
				request.salaryMax(),
				request.source(),
				request.url(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);

		JobResponse response = JobResponse.from(createJobUseCase.execute(command));
		return ResponseEntity.created(URI.create("/jobs/" + response.id())).body(response);
	}
}
