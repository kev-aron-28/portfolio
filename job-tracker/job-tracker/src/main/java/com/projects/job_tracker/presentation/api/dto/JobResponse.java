package com.projects.job_tracker.presentation.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.projects.job_tracker.domain.model.Job;

public record JobResponse(
		Long id,
		String title,
		Long companyId,
		String description,
		String location,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String source,
		String url,
		Instant createdAt) {

	public static JobResponse from(Job job) {
		return new JobResponse(
				job.id(),
				job.title(),
				job.companyId(),
				job.description(),
				job.location(),
				job.salaryMin(),
				job.salaryMax(),
				job.source(),
				job.url(),
				job.createdAt());
	}
}
