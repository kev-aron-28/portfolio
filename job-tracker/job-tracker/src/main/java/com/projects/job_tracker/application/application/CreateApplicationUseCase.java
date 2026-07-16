package com.projects.job_tracker.application.application;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.port.ApplicationRepository;
import com.projects.job_tracker.domain.port.JobRepository;

@Service
public class CreateApplicationUseCase {

	private final ApplicationRepository applicationRepository;
	private final JobRepository jobRepository;

	public CreateApplicationUseCase(ApplicationRepository applicationRepository, JobRepository jobRepository) {
		this.applicationRepository = applicationRepository;
		this.jobRepository = jobRepository;
	}

	public Application execute(CreateApplicationCommand command) {
		if (jobRepository.findById(command.jobId()).isEmpty()) {
			throw new ResourceNotFoundException("Job not found: " + command.jobId());
		}

		ApplicationStatus status = command.status() != null ? command.status() : ApplicationStatus.APPLIED;

		Application application = new Application(
				null,
				command.jobId(),
				status,
				command.appliedAt() != null ? command.appliedAt() : Instant.now(),
				command.notes());

		return applicationRepository.save(application);
	}

	public record CreateApplicationCommand(
			Long jobId,
			ApplicationStatus status,
			Instant appliedAt,
			String notes) {
	}
}
