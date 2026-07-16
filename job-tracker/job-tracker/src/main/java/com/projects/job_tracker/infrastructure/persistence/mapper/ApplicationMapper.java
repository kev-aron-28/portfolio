package com.projects.job_tracker.infrastructure.persistence.mapper;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.infrastructure.persistence.entity.ApplicationEntity;
import com.projects.job_tracker.infrastructure.persistence.entity.JobEntity;

public final class ApplicationMapper {

	private ApplicationMapper() {
	}

	public static Application toDomain(ApplicationEntity entity) {
		return new Application(
				entity.getId(),
				entity.getJob().getId(),
				ApplicationStatus.valueOf(entity.getStatus()),
				entity.getAppliedAt(),
				entity.getNotes());
	}

	public static ApplicationEntity toEntity(Application application, JobEntity job) {
		ApplicationEntity entity = new ApplicationEntity();
		entity.setId(application.id());
		entity.setJob(job);
		entity.setStatus(application.status().name());
		entity.setAppliedAt(application.appliedAt());
		entity.setNotes(application.notes());
		return entity;
	}
}
