package com.projects.job_tracker.application.job;

import static com.projects.job_tracker.testutil.TestJobs.createCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.domain.model.Company;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.port.CompanyRepository;
import com.projects.job_tracker.domain.port.JobRepository;

@ExtendWith(MockitoExtension.class)
class CreateJobUseCaseTest {

	@Mock
	private JobRepository jobRepository;

	@Mock
	private CompanyRepository companyRepository;

	@InjectMocks
	private CreateJobUseCase createJobUseCase;

	@Test
	void createsJobWithExistingCompany() {
		Company company = new Company(1L, "Acme", "https://acme.com");
		when(companyRepository.findByName("Acme")).thenReturn(java.util.Optional.of(company));
		when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
			Job job = invocation.getArgument(0);
			return new Job(
					10L,
					job.title(),
					job.companyId(),
					job.description(),
					job.location(),
					job.salaryMin(),
					job.salaryMax(),
					job.source(),
					job.url(),
					job.createdAt(),
					job.externalId(),
					job.postedAt(),
					job.employmentType(),
					job.workMode(),
					job.category(),
					job.subcategory(),
					job.benefits(),
					job.requirements());
		});

		var command = createCommand(
				"Backend Developer",
				"Acme",
				null,
				"Build APIs",
				"Remote",
				new BigDecimal("80000"),
				new BigDecimal("120000"),
				"linkedin",
				"https://example.com/job/1");

		Job result = createJobUseCase.execute(command);

		assertThat(result.id()).isEqualTo(10L);
		assertThat(result.companyId()).isEqualTo(1L);
		assertThat(result.title()).isEqualTo("Backend Developer");

		ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
		verify(jobRepository).save(jobCaptor.capture());
		assertThat(jobCaptor.getValue().companyId()).isEqualTo(1L);
	}

	@Test
	void createsCompanyWhenNotFound() {
		Company savedCompany = new Company(2L, "NewCo", "https://newco.com");
		when(companyRepository.findByName("NewCo")).thenReturn(java.util.Optional.empty());
		when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
		when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
			Job job = invocation.getArgument(0);
			return new Job(
					5L,
					job.title(),
					job.companyId(),
					job.description(),
					job.location(),
					job.salaryMin(),
					job.salaryMax(),
					job.source(),
					job.url(),
					Instant.now(),
					job.externalId(),
					job.postedAt(),
					job.employmentType(),
					job.workMode(),
					job.category(),
					job.subcategory(),
					job.benefits(),
					job.requirements());
		});

		var command = createCommand(
				"Engineer",
				"NewCo",
				"https://newco.com",
				null,
				null,
				null,
				null,
				"occ",
				"https://example.com/job/2");

		Job result = createJobUseCase.execute(command);

		assertThat(result.companyId()).isEqualTo(2L);
		verify(companyRepository).save(any(Company.class));
	}
}
