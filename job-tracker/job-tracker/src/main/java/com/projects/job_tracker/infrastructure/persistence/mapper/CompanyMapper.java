package com.projects.job_tracker.infrastructure.persistence.mapper;

import com.projects.job_tracker.domain.model.Company;
import com.projects.job_tracker.infrastructure.persistence.entity.CompanyEntity;

public final class CompanyMapper {

	private CompanyMapper() {
	}

	public static Company toDomain(CompanyEntity entity) {
		return new Company(entity.getId(), entity.getName(), entity.getWebsite());
	}

	public static CompanyEntity toEntity(Company company) {
		CompanyEntity entity = new CompanyEntity();
		entity.setId(company.id());
		entity.setName(company.name());
		entity.setWebsite(company.website());
		return entity;
	}
}
