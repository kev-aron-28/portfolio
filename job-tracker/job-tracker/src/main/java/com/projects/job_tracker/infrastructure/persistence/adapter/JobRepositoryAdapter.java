package com.projects.job_tracker.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.port.JobRepository;
import com.projects.job_tracker.infrastructure.persistence.entity.CompanyEntity;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaCompanyRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaJobRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.JobMapper;

@Repository
public class JobRepositoryAdapter implements JobRepository {

	private final JpaJobRepository jpaJobRepository;
	private final JpaCompanyRepository jpaCompanyRepository;

	public JobRepositoryAdapter(JpaJobRepository jpaJobRepository, JpaCompanyRepository jpaCompanyRepository) {
		this.jpaJobRepository = jpaJobRepository;
		this.jpaCompanyRepository = jpaCompanyRepository;
	}

	@Override
	public Job save(Job job) {
		CompanyEntity company = jpaCompanyRepository.findById(job.companyId())
				.orElseThrow(() -> new IllegalStateException("Company not found: " + job.companyId()));
		return JobMapper.toDomain(jpaJobRepository.save(JobMapper.toEntity(job, company)));
	}

	@Override
	public Optional<Job> findById(Long id) {
		return jpaJobRepository.findById(id).map(JobMapper::toDomain);
	}

	@Override
	public Optional<Job> findBySourceAndUrl(String source, String url) {
		return jpaJobRepository.findBySourceAndUrl(source, url).map(JobMapper::toDomain);
	}

	@Override
	public List<Job> findAll() {
		return jpaJobRepository.findAll().stream().map(JobMapper::toDomain).toList();
	}
}
