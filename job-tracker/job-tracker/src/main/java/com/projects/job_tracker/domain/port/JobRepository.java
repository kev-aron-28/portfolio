package com.projects.job_tracker.domain.port;

import java.util.List;
import java.util.Optional;

import com.projects.job_tracker.domain.model.Job;

public interface JobRepository {

	Job save(Job job);

	Optional<Job> findById(Long id);

	Optional<Job> findBySourceAndUrl(String source, String url);

	List<Job> findAll();
}
