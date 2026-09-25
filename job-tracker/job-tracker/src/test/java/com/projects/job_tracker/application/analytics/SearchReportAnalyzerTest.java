package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.FollowUpOverview;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.MarketInsights;
import com.projects.job_tracker.domain.model.SalaryInsights;
import com.projects.job_tracker.domain.model.SearchReport;

class SearchReportAnalyzerTest {

	private static final Instant NOW = Instant.parse("2026-09-19T18:00:00Z");

	@Test
	void returnsEmptyWhenThereAreNoApplications() {
		JobListing job = listing(1L, Instant.parse("2026-09-18T12:00:00Z"), "occ", "Remoto", "CDMX", null, null);
		FollowUpOverview followUp = FollowUpAnalyzer.analyze(List.of(job), List.of(), NOW);

		SearchReport report = SearchReportAnalyzer.analyze(List.of(job), List.of(), followUp, metrics(), NOW);

		assertThat(report.hasApplications()).isFalse();
		assertThat(report.findings()).isEmpty();
	}

	@Test
	void summarizesCoverageAndStuckPipeline() {
		JobListing applied = listing(1L, Instant.parse("2026-09-15T12:00:00Z"), "occ", "Remoto", "CDMX",
				BigDecimal.valueOf(25000), BigDecimal.valueOf(25000));
		JobListing extra = listing(2L, Instant.parse("2026-09-16T12:00:00Z"), "indeed", "Presencial", "GDL",
				BigDecimal.valueOf(40000), BigDecimal.valueOf(40000));
		Application application = application(1L, ApplicationStatus.APPLIED, Instant.parse("2026-09-15T15:00:00Z"));
		FollowUpOverview followUp = FollowUpAnalyzer.analyze(List.of(applied, extra), List.of(application), NOW);

		SearchReport report = SearchReportAnalyzer.analyze(
				List.of(applied, extra), List.of(application), followUp, metrics(), NOW);

		assertThat(report.hasApplications()).isTrue();
		assertThat(report.headline()).contains("1 de 2 vacantes (50%)");
		assertThat(report.headline()).contains("Ninguna ha avanzado");
		assertThat(report.byStatus()).extracting(item -> item.label()).contains("Postulado");
		assertThat(report.waitByStatus()).isNotEmpty();
		assertThat(report.findings()).anyMatch(finding -> finding.title().contains("pipeline"));
		assertThat(report.appliedByWorkMode()).extracting(item -> item.label()).contains("Remoto");
	}

	@Test
	void measuresMedianDaysUntilStatusChanged() {
		JobListing job = listing(3L, Instant.parse("2026-08-01T12:00:00Z"), "occ", "Híbrido", "CDMX",
				BigDecimal.valueOf(30000), BigDecimal.valueOf(30000));
		Application application = new Application(
				1L,
				3L,
				ApplicationStatus.SCREENING,
				Instant.parse("2026-08-01T12:00:00Z"),
				Instant.parse("2026-08-11T12:00:00Z"),
				null);
		FollowUpOverview followUp = FollowUpAnalyzer.analyze(List.of(job), List.of(application), NOW);

		SearchReport report = SearchReportAnalyzer.analyze(List.of(job), List.of(application), followUp, metrics(), NOW);

		assertThat(report.headline()).contains("1 ya cambió de estado");
		assertThat(report.snapshot()).anyMatch(metric -> "Respuesta".equals(metric.label()) && "10 días".equals(metric.value()));
		assertThat(report.findings()).anyMatch(finding -> finding.title().contains("primer movimiento"));
	}

	private static DashboardMetrics metrics() {
		SalaryInsights salary = new SalaryInsights(
				2, 0, BigDecimal.valueOf(30000), BigDecimal.valueOf(30000),
				BigDecimal.valueOf(25000), BigDecimal.valueOf(40000), Map.of());
		return new DashboardMetrics(
				2, 1, 0, 0, Map.of(), new EnumMap<>(ApplicationStatus.class),
				new MarketInsights(salary, 2, Map.of(), Map.of(), Map.of(), Map.of(), List.of(), List.of(), List.of()));
	}

	private static Application application(Long jobId, ApplicationStatus status, Instant at) {
		return new Application(1L, jobId, status, at, at, null);
	}

	private static JobListing listing(
			Long id,
			Instant createdAt,
			String source,
			String workMode,
			String location,
			BigDecimal salaryMin,
			BigDecimal salaryMax) {
		return new JobListing(
				id,
				"Rol " + id,
				"Acme",
				location,
				source,
				salaryMin,
				salaryMax,
				createdAt,
				null,
				workMode,
				null,
				null,
				null,
				null,
				"https://example.com/" + id,
				null);
	}
}
