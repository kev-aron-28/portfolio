package com.projects.job_tracker.infrastructure.persistence.mapper;

import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.infrastructure.persistence.entity.CompanyEntity;
import com.projects.job_tracker.infrastructure.persistence.entity.JobEntity;

public final class JobMapper {

	private JobMapper() {
	}

	public static Job toDomain(JobEntity entity) {
		return new Job(
				entity.getId(),
				entity.getTitle(),
				entity.getCompany().getId(),
				entity.getDescription(),
				entity.getLocation(),
				entity.getSalaryMin(),
				entity.getSalaryMax(),
				entity.getSource(),
				entity.getUrl(),
				entity.getCreatedAt(),
				entity.getExternalId(),
				entity.getPostedAt(),
				entity.getEmploymentType(),
				entity.getWorkMode(),
				entity.getCategory(),
				entity.getSubcategory(),
				entity.getBenefits(),
				entity.getRequirements());
	}

	public static JobEntity toEntity(Job job, CompanyEntity company) {
		JobEntity entity = new JobEntity();
		entity.setId(job.id());
		entity.setTitle(job.title());
		entity.setCompany(company);
		entity.setDescription(job.description());
		entity.setLocation(job.location());
		entity.setSalaryMin(job.salaryMin());
		entity.setSalaryMax(job.salaryMax());
		entity.setSource(job.source());
		entity.setUrl(job.url());
		entity.setCreatedAt(job.createdAt());
		entity.setExternalId(job.externalId());
		entity.setPostedAt(job.postedAt());
		entity.setEmploymentType(job.employmentType());
		entity.setWorkMode(job.workMode());
		entity.setCategory(job.category());
		entity.setSubcategory(job.subcategory());
		entity.setBenefits(job.benefits());
		entity.setRequirements(job.requirements());
		return entity;
	}
}
