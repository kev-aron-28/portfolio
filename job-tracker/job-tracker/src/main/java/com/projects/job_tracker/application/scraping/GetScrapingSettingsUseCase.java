package com.projects.job_tracker.application.scraping;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.domain.port.ScrapingSettingsRepository;

@Service
public class GetScrapingSettingsUseCase {

	private final ScrapingSettingsRepository scrapingSettingsRepository;

	public GetScrapingSettingsUseCase(ScrapingSettingsRepository scrapingSettingsRepository) {
		this.scrapingSettingsRepository = scrapingSettingsRepository;
	}

	public ScrapingSettings execute() {
		return scrapingSettingsRepository.get();
	}
}
