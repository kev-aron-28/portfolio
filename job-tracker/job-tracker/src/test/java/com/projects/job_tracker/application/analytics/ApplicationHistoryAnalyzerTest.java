package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationHistory;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.JobListing;

class ApplicationHistoryAnalyzerTest {

	@Test
	void groupsApplicationsByAppliedDateNotJobCreatedDate() {
		JobListing olderJob = listing(1L, "Backend", Instant.parse("2026-08-01T12:00:00Z"));
		JobListing newerJob = listing(2L, "Frontend", Instant.parse("2026-09-18T12:00:00Z"));
		Application laterApply = application(1L, Instant.parse("2026-09-20T18:00:00Z"));
		Application earlierApply = application(2L, Instant.parse("2026-09-10T18:00:00Z"));

		ApplicationHistory history = ApplicationHistoryAnalyzer.analyze(
				List.of(olderJob, newerJob),
				List.of(laterApply, earlierApply));

		assertThat(history.total()).isEqualTo(2);
		assertThat(history.months()).extracting(month -> month.month())
				.containsExactly(YearMonth.of(2026, 9));
		assertThat(history.months().get(0).days()).extracting(day -> day.date())
				.containsExactly(LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 10));
		assertThat(history.months().get(0).days().get(0).items().get(0).companyName()).isEqualTo("Acme");
		assertThat(history.months().get(0).days().get(0).items().get(0).title()).isEqualTo("Backend");
	}

	@Test
	void ignoresJobsWithoutApplications() {
		JobListing job = listing(3L, "Ignored", Instant.parse("2026-09-01T12:00:00Z"));

		ApplicationHistory history = ApplicationHistoryAnalyzer.analyze(List.of(job), List.of());

		assertThat(history.total()).isZero();
		assertThat(history.months()).isEmpty();
	}

	private static Application application(Long jobId, Instant appliedAt) {
		return new Application(1L, jobId, ApplicationStatus.APPLIED, appliedAt, appliedAt, null);
	}

	private static JobListing listing(Long id, String title, Instant createdAt) {
		return new JobListing(
				id,
				title,
				"Acme",
				"CDMX",
				"occ",
				BigDecimal.ONE,
				BigDecimal.TEN,
				createdAt,
				null,
				null,
				null,
				null,
				null,
				null,
				"https://example.com/" + id,
				null);
	}
}
