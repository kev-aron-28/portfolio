package com.projects.job_tracker.domain.port;

import java.util.List;
import java.util.Optional;

import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.JobDetail;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.domain.model.JobListing;

public interface JobReadRepository {

	List<JobListing> findListings(JobFilterCriteria filters);

	Optional<JobDetail> findDetailById(Long id);

	DashboardMetrics getDashboardMetrics();
}
