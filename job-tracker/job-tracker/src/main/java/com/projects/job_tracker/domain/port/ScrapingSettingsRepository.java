package com.projects.job_tracker.domain.port;

import com.projects.job_tracker.domain.model.ScrapingSettings;

public interface ScrapingSettingsRepository {

	ScrapingSettings get();

	ScrapingSettings save(ScrapingSettings settings);
}
