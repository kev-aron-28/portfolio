package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.JobGroupField;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobListingGroup;

class JobListingPresenterTest {

	@Test
	void groupsBySourceAndBuildsOverview() {
		List<JobListing> jobs = List.of(
				listing(1L, "Java A", "Acme", "CDMX", "linkedin", "remote", null),
				listing(2L, "Java B", "Beta", "GDL", "occ", "hybrid", ApplicationStatus.APPLIED),
				listing(3L, "Java C", "Gamma", "CDMX", "linkedin", "remote", null));

		var overview = JobListingPresenter.present(jobs, JobGroupField.SOURCE);

		assertThat(overview.total()).isEqualTo(3);
		assertThat(overview.unapplied()).isEqualTo(2);
		assertThat(overview.bySource()).containsEntry("linkedin", 2).containsEntry("occ", 1);
		assertThat(overview.groups()).hasSize(2);
		assertThat(overview.groups().get(0).label()).isEqualTo("linkedin");
		assertThat(overview.groups().get(0).count()).isEqualTo(2);
	}

	@Test
	void groupsByApplicationStatus() {
		List<JobListing> jobs = List.of(
				listing(1L, "A", "Acme", "CDMX", "occ", null, null),
				listing(2L, "B", "Beta", "GDL", "occ", null, ApplicationStatus.SCREENING));

		var groups = JobListingPresenter.group(jobs, JobGroupField.APPLICATION_STATUS);

		assertThat(groups).extracting(group -> group.label())
				.containsExactlyInAnyOrder("Sin postular", "SCREENING");
	}

	@Test
	void groupsByTechnologyAndAllowsMultipleGroupsPerJob() {
		List<JobListing> jobs = List.of(
				listing(1L, "Java Developer", "Acme", "CDMX", "occ", null, "Spring Boot, PostgreSQL", null, null),
				listing(2L, "Frontend React", "Beta", "GDL", "linkedin", null, "TypeScript", null, null),
				listing(3L, "Consultor", "Gamma", "CDMX", "occ", null, null, null, null));

		var groups = JobListingPresenter.group(jobs, JobGroupField.TECHNOLOGY);

		assertThat(groups).extracting(group -> group.label())
				.contains("Java", "Spring Boot", "PostgreSQL", "React", "TypeScript", "Sin tecnología detectada");
		assertThat(groups.stream().filter(group -> group.label().equals("Java")).findFirst())
				.get()
				.extracting(group -> group.count())
				.isEqualTo(1);
		assertThat(groups.stream().filter(group -> group.label().equals("Spring Boot")).findFirst())
				.get()
				.extracting(JobListingGroup::jobs)
				.asList()
				.extracting(job -> ((JobListing) job).id())
				.containsExactly(1L);
	}

	private JobListing listing(
			Long id,
			String title,
			String company,
			String location,
			String source,
			String workMode,
			ApplicationStatus status) {
		return listing(id, title, company, location, source, workMode, null, null, status);
	}

	private JobListing listing(
			Long id,
			String title,
			String company,
			String location,
			String source,
			String workMode,
			String requirements,
			String description,
			ApplicationStatus status) {
		return new JobListing(
				id,
				title,
				company,
				location,
				source,
				BigDecimal.valueOf(30000),
				BigDecimal.valueOf(50000),
				Instant.parse("2026-01-01T00:00:00Z"),
				null,
				workMode,
				null,
				null,
				requirements,
				description,
				"https://example.com/" + id,
				status);
	}
}
