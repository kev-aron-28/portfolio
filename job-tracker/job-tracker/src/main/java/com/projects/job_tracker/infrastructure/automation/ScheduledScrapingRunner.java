package com.projects.job_tracker.infrastructure.automation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projects.job_tracker.application.automation.RunScheduledScrapingUseCase;
import com.projects.job_tracker.infrastructure.scraping.ScrapingSettingsHolder;

@Component
public class ScheduledScrapingRunner {

	private final RunScheduledScrapingUseCase runScheduledScrapingUseCase;
	private final ScrapingSettingsHolder settingsHolder;
	private long lastPollAt;

	public ScheduledScrapingRunner(
			RunScheduledScrapingUseCase runScheduledScrapingUseCase,
			ScrapingSettingsHolder settingsHolder) {
		this.runScheduledScrapingUseCase = runScheduledScrapingUseCase;
		this.settingsHolder = settingsHolder;
	}

	@Scheduled(fixedDelay = 15000)
	public void pollDueSchedules() {
		if (!settingsHolder.schedulingEnabled()) {
			return;
		}
		long now = System.currentTimeMillis();
		if (now - lastPollAt < settingsHolder.schedulingPollIntervalMs()) {
			return;
		}
		lastPollAt = now;
		runScheduledScrapingUseCase.executeAllDue();
	}
}
