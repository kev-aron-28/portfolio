package com.projects.job_tracker.domain.port;

import java.util.List;
import java.util.Optional;

import com.projects.job_tracker.domain.model.Application;

public interface ApplicationRepository {

	Application save(Application application);

	Optional<Application> findById(Long id);

	Optional<Application> findByJobId(Long jobId);

	List<Application> findAll();
}
