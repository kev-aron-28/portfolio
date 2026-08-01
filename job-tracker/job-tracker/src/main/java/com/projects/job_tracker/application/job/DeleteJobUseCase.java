package com.projects.job_tracker.application.job;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.port.ApplicationRepository;
import com.projects.job_tracker.domain.port.JobRepository;

@Service
public class DeleteJobUseCase {

	private final JobRepository jobRepository;
	private final ApplicationRepository applicationRepository;

	public DeleteJobUseCase(JobRepository jobRepository, ApplicationRepository applicationRepository) {
		this.jobRepository = jobRepository;
		this.applicationRepository = applicationRepository;
	}

	@Transactional
	public void execute(Long id) {
		if (jobRepository.findById(id).isEmpty()) {
			throw new ResourceNotFoundException("Job not found: " + id);
		}
		deleteExisting(id);
	}

	@Transactional
	public int execute(Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			throw new IllegalArgumentException("Selecciona al menos una vacante");
		}
		Set<Long> uniqueIds = new LinkedHashSet<>();
		for (Long id : ids) {
			if (id != null) {
				uniqueIds.add(id);
			}
		}
		if (uniqueIds.isEmpty()) {
			throw new IllegalArgumentException("Selecciona al menos una vacante");
		}
		int deleted = 0;
		for (Long id : uniqueIds) {
			if (jobRepository.findById(id).isPresent()) {
				deleteExisting(id);
				deleted++;
			}
		}
		return deleted;
	}

	private void deleteExisting(Long id) {
		applicationRepository.deleteByJobId(id);
		jobRepository.deleteById(id);
	}
}
