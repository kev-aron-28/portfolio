package com.projects.job_tracker.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;

class ScrapingScheduleTest {

	@Test
	void isDueWhenNeverRun() {
		var schedule = new ScrapingSchedule(
				1L, 1L, List.of(JobPlatform.OCC), 60, 20, true, null, Instant.now());

		assertThat(schedule.isDue(Instant.now())).isTrue();
	}

	@Test
	void isNotDueBeforeIntervalElapses() {
		Instant lastRun = Instant.parse("2026-01-01T10:00:00Z");
		var schedule = new ScrapingSchedule(
				1L, 1L, List.of(JobPlatform.OCC), 60, 20, true, lastRun, Instant.now());

		assertThat(schedule.isDue(lastRun.plus(30, ChronoUnit.MINUTES))).isFalse();
	}

	@Test
	void isDueAfterIntervalElapses() {
		Instant lastRun = Instant.parse("2026-01-01T10:00:00Z");
		var schedule = new ScrapingSchedule(
				1L, 1L, List.of(JobPlatform.OCC), 60, 20, true, lastRun, Instant.now());

		assertThat(schedule.isDue(lastRun.plus(60, ChronoUnit.MINUTES))).isTrue();
	}

	@Test
	void disabledScheduleIsNeverDue() {
		var schedule = new ScrapingSchedule(
				1L, 1L, List.of(JobPlatform.OCC), 60, 20, false, null, Instant.now());

		assertThat(schedule.isDue(Instant.now())).isFalse();
	}
}
