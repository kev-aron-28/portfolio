package com.projects.job_tracker.application.automation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.domain.model.ImportedJobSummary;
import com.projects.job_tracker.domain.model.JobImportNotification;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.NotificationPublisher;
import com.projects.job_tracker.domain.port.ScrapingScheduleRepository;
import com.projects.job_tracker.domain.port.SearchProfileRepository;

@ExtendWith(MockitoExtension.class)
class RunScheduledScrapingUseCaseTest {

	@Mock
	private ScrapingScheduleRepository scrapingScheduleRepository;

	@Mock
	private SearchProfileRepository searchProfileRepository;

	@Mock
	private ScrapeJobsUseCase scrapeJobsUseCase;

	@Mock
	private NotificationPublisher notificationPublisher;

	@InjectMocks
	private RunScheduledScrapingUseCase runScheduledScrapingUseCase;

	@Test
	void runsDueSchedulesAndPublishesNotification() {
		Instant now = Instant.parse("2026-07-06T20:00:00Z");
		ScrapingSchedule schedule = new ScrapingSchedule(
				1L, 10L, List.of(JobPlatform.OCC), 30, 15, true, null, now);
		SearchProfile profile = new SearchProfile(10L, "Backend", "java", null);

		when(scrapingScheduleRepository.findAllEnabled()).thenReturn(List.of(schedule));
		when(searchProfileRepository.findById(10L)).thenReturn(java.util.Optional.of(profile));
		when(scrapeJobsUseCase.execute(any()))
				.thenReturn(new ScrapeJobsUseCase.ScrapeResult(
						5,
						2,
						1,
						List.of(),
						List.of(new ImportedJobSummary("Java Dev", "Acme", "occ", "https://example.com/1"))));

		var summary = runScheduledScrapingUseCase.executeAllDue();

		assertThat(summary.schedulesRun()).isEqualTo(1);
		assertThat(summary.results().getFirst().imported()).isEqualTo(2);
		verify(scrapingScheduleRepository).save(any());
		verify(notificationPublisher).publish(any(JobImportNotification.class));
	}

	@Test
	void skipsSchedulesThatAreNotDue() {
		Instant lastRun = Instant.now().minus(10, ChronoUnit.MINUTES);
		ScrapingSchedule schedule = new ScrapingSchedule(
				1L, 10L, List.of(JobPlatform.OCC), 30, 15, true, lastRun, lastRun);

		when(scrapingScheduleRepository.findAllEnabled()).thenReturn(List.of(schedule));

		var summary = runScheduledScrapingUseCase.executeAllDue();

		assertThat(summary.schedulesRun()).isZero();
		verify(scrapeJobsUseCase, never()).execute(any());
		verify(notificationPublisher, never()).publish(any());
	}

	@Test
	void doesNotPublishWhenNothingImportedAndNoErrors() {
		ScrapingSchedule schedule = new ScrapingSchedule(
				2L, 10L, List.of(JobPlatform.OCC), 30, 15, true, null, Instant.now());
		SearchProfile profile = new SearchProfile(10L, "Backend", "java", null);

		when(scrapingScheduleRepository.findAllEnabled()).thenReturn(List.of(schedule));
		when(searchProfileRepository.findById(10L)).thenReturn(java.util.Optional.of(profile));
		when(scrapeJobsUseCase.execute(any()))
				.thenReturn(new ScrapeJobsUseCase.ScrapeResult(3, 0, 3, List.of(), List.of()));

		runScheduledScrapingUseCase.executeAllDue();

		verify(notificationPublisher, never()).publish(any());
	}
}
