package com.projects.job_tracker.application.analytics;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.port.JobReadRepository;

@Service
public class GetDashboardMetricsUseCase {

	private final JobReadRepository jobReadRepository;

	public GetDashboardMetricsUseCase(JobReadRepository jobReadRepository) {
		this.jobReadRepository = jobReadRepository;
	}

	public DashboardMetrics execute() {
		return jobReadRepository.getDashboardMetrics();
	}
}
