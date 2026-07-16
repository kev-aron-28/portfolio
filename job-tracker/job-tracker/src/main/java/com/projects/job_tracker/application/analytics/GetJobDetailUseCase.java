package com.projects.job_tracker.application.analytics;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.JobDetail;
import com.projects.job_tracker.domain.port.JobReadRepository;

@Service
public class GetJobDetailUseCase {

	private final JobReadRepository jobReadRepository;

	public GetJobDetailUseCase(JobReadRepository jobReadRepository) {
		this.jobReadRepository = jobReadRepository;
	}

	public JobDetail execute(Long id) {
		return jobReadRepository.findDetailById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
	}
}
