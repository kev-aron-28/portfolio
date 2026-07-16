package com.projects.job_tracker.application.automation;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.domain.port.ScrapingScheduleRepository;
import com.projects.job_tracker.domain.port.SearchProfileRepository;

@Service
public class CreateScrapingScheduleUseCase {

	private final ScrapingScheduleRepository scrapingScheduleRepository;
	private final SearchProfileRepository searchProfileRepository;

	public CreateScrapingScheduleUseCase(
			ScrapingScheduleRepository scrapingScheduleRepository,
			SearchProfileRepository searchProfileRepository) {
		this.scrapingScheduleRepository = scrapingScheduleRepository;
		this.searchProfileRepository = searchProfileRepository;
	}

	public ScrapingSchedule execute(CreateScrapingScheduleCommand command) {
		if (searchProfileRepository.findById(command.profileId()).isEmpty()) {
			throw new ResourceNotFoundException("Profile not found: " + command.profileId());
		}

		ScrapingSchedule schedule = new ScrapingSchedule(
				null,
				command.profileId(),
				command.platforms(),
				command.intervalMinutes(),
				command.maxResults(),
				true,
				null,
				Instant.now());

		return scrapingScheduleRepository.save(schedule);
	}

	public record CreateScrapingScheduleCommand(
			Long profileId, List<JobPlatform> platforms, int intervalMinutes, int maxResults) {
	}
}
