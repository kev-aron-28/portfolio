package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.application.job.DeleteJobUseCase;
import com.projects.job_tracker.application.job.GetJobUseCase;
import com.projects.job_tracker.application.job.ListJobsUseCase;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;
import com.projects.job_tracker.presentation.api.dto.BulkDeleteJobsRequest;
import com.projects.job_tracker.presentation.api.dto.CreateJobRequest;
import com.projects.job_tracker.presentation.api.dto.JobResponse;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

	private final CreateJobUseCase createJobUseCase;
	private final DeleteJobUseCase deleteJobUseCase;
	private final GetJobUseCase getJobUseCase;
	private final ListJobsUseCase listJobsUseCase;
	private final MarketSegmentRepository marketSegmentRepository;

	public JobController(
			CreateJobUseCase createJobUseCase,
			DeleteJobUseCase deleteJobUseCase,
			GetJobUseCase getJobUseCase,
			ListJobsUseCase listJobsUseCase,
			MarketSegmentRepository marketSegmentRepository) {
		this.createJobUseCase = createJobUseCase;
		this.deleteJobUseCase = deleteJobUseCase;
		this.getJobUseCase = getJobUseCase;
		this.listJobsUseCase = listJobsUseCase;
		this.marketSegmentRepository = marketSegmentRepository;
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
		Job created = createJobUseCase.execute(request.toCommand());
		if (request.segmentIds() != null) {
			for (Long segmentId : request.segmentIds()) {
				if (segmentId != null) {
					marketSegmentRepository.attachJob(segmentId, created.id());
				}
			}
		}
		JobResponse response = JobResponse.from(created);
		return ResponseEntity.created(URI.create("/api/jobs/" + response.id())).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
		deleteJobUseCase.execute(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/bulk-delete")
	public ResponseEntity<Map<String, Integer>> bulkDeleteJobs(@RequestBody BulkDeleteJobsRequest request) {
		int deleted = deleteJobUseCase.execute(request.ids());
		return ResponseEntity.ok(Map.of("deleted", deleted));
	}
}
