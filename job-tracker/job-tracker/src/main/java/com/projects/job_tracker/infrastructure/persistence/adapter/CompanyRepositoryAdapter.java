package com.projects.job_tracker.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.projects.job_tracker.domain.model.Company;
import com.projects.job_tracker.domain.port.CompanyRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaCompanyRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.CompanyMapper;

@Repository
public class CompanyRepositoryAdapter implements CompanyRepository {

	private final JpaCompanyRepository jpaCompanyRepository;

	public CompanyRepositoryAdapter(JpaCompanyRepository jpaCompanyRepository) {
		this.jpaCompanyRepository = jpaCompanyRepository;
	}

	@Override
	public Company save(Company company) {
		return CompanyMapper.toDomain(jpaCompanyRepository.save(CompanyMapper.toEntity(company)));
	}

	@Override
	public Optional<Company> findByName(String name) {
		return jpaCompanyRepository.findByName(name).map(CompanyMapper::toDomain);
	}
}
