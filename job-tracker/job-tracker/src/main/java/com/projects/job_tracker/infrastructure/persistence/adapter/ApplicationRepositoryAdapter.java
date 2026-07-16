package com.projects.job_tracker.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.port.ApplicationRepository;
import com.projects.job_tracker.infrastructure.persistence.entity.JobEntity;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaApplicationRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaJobRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.ApplicationMapper;

@Repository
public class ApplicationRepositoryAdapter implements ApplicationRepository {

	private final JpaApplicationRepository jpaApplicationRepository;
	private final JpaJobRepository jpaJobRepository;

	public ApplicationRepositoryAdapter(
			JpaApplicationRepository jpaApplicationRepository,
			JpaJobRepository jpaJobRepository) {
		this.jpaApplicationRepository = jpaApplicationRepository;
		this.jpaJobRepository = jpaJobRepository;
	}

	@Override
	public Application save(Application application) {
		JobEntity job = jpaJobRepository.findById(application.jobId())
				.orElseThrow(() -> new IllegalStateException("Job not found: " + application.jobId()));
		return ApplicationMapper.toDomain(
				jpaApplicationRepository.save(ApplicationMapper.toEntity(application, job)));
	}

	@Override
	public Optional<Application> findById(Long id) {
		return jpaApplicationRepository.findById(id).map(ApplicationMapper::toDomain);
	}

	@Override
	public Optional<Application> findByJobId(Long jobId) {
		return jpaApplicationRepository.findFirstByJob_IdOrderByAppliedAtDesc(jobId).map(ApplicationMapper::toDomain);
	}

	@Override
	public List<Application> findAll() {
		return jpaApplicationRepository.findAll().stream().map(ApplicationMapper::toDomain).toList();
	}
}
