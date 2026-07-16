package com.projects.job_tracker.application.scraping;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.domain.port.ScrapingSettingsRepository;
import com.projects.job_tracker.infrastructure.scraping.ScrapingSettingsHolder;

@Service
public class UpdateScrapingSettingsUseCase {

	private final ScrapingSettingsRepository scrapingSettingsRepository;
	private final ScrapingSettingsHolder settingsHolder;

	public UpdateScrapingSettingsUseCase(
			ScrapingSettingsRepository scrapingSettingsRepository,
			ScrapingSettingsHolder settingsHolder) {
		this.scrapingSettingsRepository = scrapingSettingsRepository;
		this.settingsHolder = settingsHolder;
	}

	public ScrapingSettings execute(UpdateScrapingSettingsCommand command) {
		ScrapingSettings updated = new ScrapingSettings(
				command.rateLimitMs(),
				command.defaultMaxResults(),
				command.defaultPlatforms(),
				blankToNull(command.linkedinStorageStatePath()),
				command.linkedinPageTimeoutMs(),
				blankToNull(command.occStorageStatePath()),
				command.occPageTimeoutMs(),
				blankToNull(command.indeedStorageStatePath()),
				command.indeedPageTimeoutMs(),
				blankToNull(command.computrabajoStorageStatePath()),
				command.computrabajoPageTimeoutMs(),
				command.schedulingEnabled(),
				command.schedulingPollIntervalMs());

		ScrapingSettings saved = scrapingSettingsRepository.save(updated);
		settingsHolder.refresh(saved);
		return saved;
	}

	private String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

	public record UpdateScrapingSettingsCommand(
			int rateLimitMs,
			int defaultMaxResults,
			List<JobPlatform> defaultPlatforms,
			String linkedinStorageStatePath,
			int linkedinPageTimeoutMs,
			String occStorageStatePath,
			int occPageTimeoutMs,
			String indeedStorageStatePath,
			int indeedPageTimeoutMs,
			String computrabajoStorageStatePath,
			int computrabajoPageTimeoutMs,
			boolean schedulingEnabled,
			long schedulingPollIntervalMs) {
	}
}
