package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.FollowUpOverview;
import com.projects.job_tracker.domain.model.FollowUpSignal;
import com.projects.job_tracker.domain.model.JobListing;

class FollowUpAnalyzerTest {

	private static final Instant NOW = Instant.parse("2026-09-19T18:00:00Z");

	@Test
	void marksAppliedAsSilentAfterThreeWeeks() {
		JobListing job = listing(1L, "Java Dev", Instant.parse("2026-08-20T15:00:00Z"));
		Application application = application(1L, ApplicationStatus.APPLIED, Instant.parse("2026-08-20T16:00:00Z"));

		FollowUpOverview overview = FollowUpAnalyzer.analyze(List.of(job), List.of(application), NOW);

		assertThat(overview.likelySilent()).isEqualTo(1);
		assertThat(overview.attention()).hasSize(1);
		assertThat(overview.attention().get(0).signal()).isEqualTo(FollowUpSignal.LIKELY_SILENT);
		assertThat(overview.attention().get(0).daysInStatus()).isEqualTo(30);
		assertThat(overview.attention().get(0).insight()).contains("ya no se comuniquen");
	}

	@Test
	void keepsRecentAppliedAsActive() {
		JobListing job = listing(2L, "Recent", Instant.parse("2026-09-15T15:00:00Z"));
		Application application = application(2L, ApplicationStatus.APPLIED, Instant.parse("2026-09-15T16:00:00Z"));

		FollowUpOverview overview = FollowUpAnalyzer.analyze(List.of(job), List.of(application), NOW);

		assertThat(overview.active()).isEqualTo(1);
		assertThat(overview.attention()).isEmpty();
		assertThat(overview.days()).hasSize(1);
		assertThat(overview.days().get(0).date()).isEqualTo(LocalDate.of(2026, 9, 15));
	}

	@Test
	void groupsByRegisteredDateAndFlagsUnapplied() {
		JobListing older = listing(3L, "Older", Instant.parse("2026-09-10T12:00:00Z"));
		JobListing newer = listing(4L, "Newer", Instant.parse("2026-09-18T12:00:00Z"));

		FollowUpOverview overview = FollowUpAnalyzer.analyze(List.of(older, newer), List.of(), NOW);

		assertThat(overview.days()).extracting(day -> day.date())
				.containsExactly(LocalDate.of(2026, 9, 18), LocalDate.of(2026, 9, 10));
		assertThat(overview.days().get(0).entries().get(0).signal()).isEqualTo(FollowUpSignal.UNAPPLIED);
		assertThat(overview.closed()).isZero();
	}

	@Test
	void treatsRejectedAsClosed() {
		JobListing job = listing(5L, "Closed", Instant.parse("2026-07-01T12:00:00Z"));
		Application application = application(5L, ApplicationStatus.REJECTED, Instant.parse("2026-07-02T12:00:00Z"));

		FollowUpOverview overview = FollowUpAnalyzer.analyze(List.of(job), List.of(application), NOW);

		assertThat(overview.closed()).isEqualTo(1);
		assertThat(overview.attention()).isEmpty();
		assertThat(overview.days().get(0).entries().get(0).signal()).isEqualTo(FollowUpSignal.CLOSED);
	}

	@Test
	void usesStatusChangedAtForDwellTime() {
		JobListing job = listing(6L, "Interview", Instant.parse("2026-08-01T12:00:00Z"));
		Application application = new Application(
				1L,
				6L,
				ApplicationStatus.INTERVIEWING,
				Instant.parse("2026-08-01T12:00:00Z"),
				Instant.parse("2026-09-16T12:00:00Z"),
				null);

		FollowUpOverview overview = FollowUpAnalyzer.analyze(List.of(job), List.of(application), NOW);

		assertThat(overview.active()).isEqualTo(1);
		assertThat(overview.days().get(0).entries().get(0).daysInStatus()).isEqualTo(3);
	}

	private static Application application(Long jobId, ApplicationStatus status, Instant at) {
		return new Application(1L, jobId, status, at, at, null);
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
