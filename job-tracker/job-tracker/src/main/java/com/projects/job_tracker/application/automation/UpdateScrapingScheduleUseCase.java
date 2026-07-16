package com.projects.job_tracker.application.automation;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.domain.port.ScrapingScheduleRepository;

@Service
public class UpdateScrapingScheduleUseCase {

	private final ScrapingScheduleRepository scrapingScheduleRepository;

	public UpdateScrapingScheduleUseCase(ScrapingScheduleRepository scrapingScheduleRepository) {
		this.scrapingScheduleRepository = scrapingScheduleRepository;
	}

	public ScrapingSchedule execute(Long id, UpdateScrapingScheduleCommand command) {
		ScrapingSchedule existing = scrapingScheduleRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + id));

		boolean enabled = command.enabled() != null ? command.enabled() : existing.enabled();
		ScrapingSchedule updated = existing.withEnabled(enabled);
		return scrapingScheduleRepository.save(updated);
	}

	public record UpdateScrapingScheduleCommand(Boolean enabled) {
	}
}
