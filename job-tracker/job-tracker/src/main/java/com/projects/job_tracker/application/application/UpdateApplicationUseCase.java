package com.projects.job_tracker.application.application;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.port.ApplicationRepository;

@Service
public class UpdateApplicationUseCase {

	private final ApplicationRepository applicationRepository;

	public UpdateApplicationUseCase(ApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	public Application execute(Long id, UpdateApplicationCommand command) {
		Application existing = applicationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));

		ApplicationStatus status = command.status() != null ? command.status() : existing.status();
		String notes = command.notes() != null ? command.notes() : existing.notes();
		Instant statusChangedAt = status == existing.status()
				? existing.statusSince()
				: Instant.now();

		Application updated = new Application(
				existing.id(),
				existing.jobId(),
				status,
				existing.appliedAt(),
				statusChangedAt,
				notes);
		return applicationRepository.save(updated);
	}

	public record UpdateApplicationCommand(ApplicationStatus status, String notes) {
	}
}
