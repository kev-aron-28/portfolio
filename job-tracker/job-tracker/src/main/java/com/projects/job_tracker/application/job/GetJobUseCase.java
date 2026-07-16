package com.projects.job_tracker.application.job;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.port.JobRepository;

@Service
public class GetJobUseCase {

	private final JobRepository jobRepository;

	public GetJobUseCase(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	public Job execute(Long id) {
		return jobRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
	}
}
