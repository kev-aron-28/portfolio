package com.projects.job_tracker.infrastructure.scraping;

import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.domain.port.ScrapingSettingsRepository;

@Component
public class ScrapingSettingsHolder {

	private volatile ScrapingSettings settings;

	public ScrapingSettingsHolder(ScrapingSettingsRepository scrapingSettingsRepository) {
		this.settings = scrapingSettingsRepository.get();
	}

	public ScrapingSettings get() {
		return settings;
	}

	public void refresh(ScrapingSettings updated) {
		this.settings = updated;
	}

	public long rateLimitMs() {
		return settings.rateLimitMs();
	}

	public String linkedinStorageStatePath() {
		return settings.linkedinStorageStatePath();
	}

	public int linkedinPageTimeoutMs() {
		return settings.linkedinPageTimeoutMs();
	}

	public String occStorageStatePath() {
		return settings.occStorageStatePath();
	}

	public int occPageTimeoutMs() {
		return settings.occPageTimeoutMs();
	}

	public String indeedStorageStatePath() {
		return settings.indeedStorageStatePath();
	}

	public int indeedPageTimeoutMs() {
		return settings.indeedPageTimeoutMs();
	}

	public String computrabajoStorageStatePath() {
		return settings.computrabajoStorageStatePath();
	}

	public int computrabajoPageTimeoutMs() {
		return settings.computrabajoPageTimeoutMs();
	}

	public boolean schedulingEnabled() {
		return settings.schedulingEnabled();
	}

	public long schedulingPollIntervalMs() {
		return settings.schedulingPollIntervalMs();
	}
}
