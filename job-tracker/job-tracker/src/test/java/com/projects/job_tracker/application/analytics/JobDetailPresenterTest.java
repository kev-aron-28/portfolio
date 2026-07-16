package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.model.JobDetail;

class JobDetailPresenterTest {

	@Test
	void buildsRichViewModel() {
		Job job = new Job(
				1L,
				"Senior Java Developer",
				2L,
				"Desarrollo de microservicios.",
				"Ciudad de México",
				BigDecimal.valueOf(45000),
				BigDecimal.valueOf(60000),
				"linkedin",
				"https://example.com/job",
				Instant.parse("2026-01-01T00:00:00Z"),
				"ext-1",
				Instant.parse("2025-12-20T00:00:00Z"),
				"Tiempo completo",
				"remote",
				"Tecnología",
				"Backend",
				"Prestaciones superiores",
				"Java, Spring Boot, 5 años de experiencia");
		JobDetail detail = new JobDetail(job, "Acme Corp", "https://acme.com", Optional.empty());

		var view = JobDetailPresenter.present(detail);

		assertThat(view.subtitle()).isEqualTo("Acme Corp · Ciudad de México · Remoto");
		assertThat(view.workModeLabel()).isEqualTo("Remoto");
		assertThat(view.employmentTypeLabel()).isEqualTo("Tiempo completo");
		assertThat(view.salaryLabel()).contains("45,000").contains("60,000").contains("MXN/mes");
		assertThat(view.technologies()).contains("Java", "Spring Boot");
		assertThat(view.experienceLevel()).isEqualTo("Senior");
		assertThat(view.hasContent()).isTrue();
	}
}
