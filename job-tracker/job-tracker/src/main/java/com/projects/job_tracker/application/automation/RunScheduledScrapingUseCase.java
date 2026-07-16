package com.projects.job_tracker.application.automation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.JobImportNotification;
import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.NotificationPublisher;
import com.projects.job_tracker.domain.port.ScrapingScheduleRepository;
import com.projects.job_tracker.domain.port.SearchProfileRepository;

@Service
public class RunScheduledScrapingUseCase {

	private final ScrapingScheduleRepository scrapingScheduleRepository;
	private final SearchProfileRepository searchProfileRepository;
	private final ScrapeJobsUseCase scrapeJobsUseCase;
	private final NotificationPublisher notificationPublisher;

	public RunScheduledScrapingUseCase(
			ScrapingScheduleRepository scrapingScheduleRepository,
			SearchProfileRepository searchProfileRepository,
			ScrapeJobsUseCase scrapeJobsUseCase,
			NotificationPublisher notificationPublisher) {
		this.scrapingScheduleRepository = scrapingScheduleRepository;
		this.searchProfileRepository = searchProfileRepository;
		this.scrapeJobsUseCase = scrapeJobsUseCase;
		this.notificationPublisher = notificationPublisher;
	}

	public AutomationRunSummary executeAllDue() {
		Instant now = Instant.now();
		List<ScheduleRunResult> results = new ArrayList<>();

		for (ScrapingSchedule schedule : scrapingScheduleRepository.findAllEnabled()) {
			if (schedule.isDue(now)) {
				results.add(runSchedule(schedule, now));
			}
		}

		return new AutomationRunSummary(results.size(), results);
	}

	public ScheduleRunResult executeById(Long scheduleId) {
		ScrapingSchedule schedule = scrapingScheduleRepository
				.findById(scheduleId)
				.orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + scheduleId));
		return runSchedule(schedule, Instant.now());
	}

	private ScheduleRunResult runSchedule(ScrapingSchedule schedule, Instant now) {
		SearchProfile profile = searchProfileRepository
				.findById(schedule.profileId())
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + schedule.profileId()));

		ScrapeJobsUseCase.ScrapeResult scrapeResult = scrapeJobsUseCase.execute(new ScrapeJobsUseCase.ScrapeJobsCommand(
				schedule.profileId(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				schedule.platforms(),
				schedule.maxResults()));

		scrapingScheduleRepository.save(schedule.withLastRunAt(now));

		if (scrapeResult.imported() > 0 || !scrapeResult.errors().isEmpty()) {
			notificationPublisher.publish(new JobImportNotification(
					profile.id(),
					profile.name(),
					schedule.id(),
					scrapeResult.imported(),
					scrapeResult.duplicates(),
					scrapeResult.importedJobs(),
					scrapeResult.errors(),
					now));
		}

		return new ScheduleRunResult(
				schedule.id(),
				profile.id(),
				profile.name(),
				scrapeResult.scraped(),
				scrapeResult.imported(),
				scrapeResult.duplicates(),
				scrapeResult.errors());
	}

	public record AutomationRunSummary(int schedulesRun, List<ScheduleRunResult> results) {
	}

	public record ScheduleRunResult(
			Long scheduleId,
			Long profileId,
			String profileName,
			int scraped,
			int imported,
			int duplicates,
			List<String> errors) {
	}
}
