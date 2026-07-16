package com.projects.job_tracker.application.job;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.Company;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.port.CompanyRepository;
import com.projects.job_tracker.domain.port.JobRepository;

@Service
public class CreateJobUseCase {

	private final JobRepository jobRepository;
	private final CompanyRepository companyRepository;

	public CreateJobUseCase(JobRepository jobRepository, CompanyRepository companyRepository) {
		this.jobRepository = jobRepository;
		this.companyRepository = companyRepository;
	}

	public Job execute(CreateJobCommand command) {
		Company company = companyRepository.findByName(command.companyName())
				.orElseGet(() -> companyRepository.save(new Company(command.companyName(), command.companyWebsite())));

		Job job = new Job(
				null,
				command.title(),
				company.id(),
				command.description(),
				command.location(),
				command.salaryMin(),
				command.salaryMax(),
				command.source(),
				command.url(),
				Instant.now(),
				command.externalId(),
				command.postedAt(),
				command.employmentType(),
				command.workMode(),
				command.category(),
				command.subcategory(),
				command.benefits(),
				command.requirements());

		return jobRepository.save(job);
	}

	public record CreateJobCommand(
			String title,
			String companyName,
			String companyWebsite,
			String description,
			String location,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			String source,
			String url,
			String externalId,
			Instant postedAt,
			String employmentType,
			String workMode,
			String category,
			String subcategory,
			String benefits,
			String requirements) {
	}
}
