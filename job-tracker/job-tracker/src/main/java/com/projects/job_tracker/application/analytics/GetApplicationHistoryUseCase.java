package com.projects.job_tracker.application.analytics;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.ApplicationHistory;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.domain.port.ApplicationRepository;
import com.projects.job_tracker.domain.port.JobReadRepository;

@Service
public class GetApplicationHistoryUseCase {

	private final JobReadRepository jobReadRepository;
	private final ApplicationRepository applicationRepository;

	public GetApplicationHistoryUseCase(
			JobReadRepository jobReadRepository,
			ApplicationRepository applicationRepository) {
		this.jobReadRepository = jobReadRepository;
		this.applicationRepository = applicationRepository;
	}

	public ApplicationHistory execute() {
		return ApplicationHistoryAnalyzer.analyze(
				jobReadRepository.findListings(JobFilterCriteria.empty()),
				applicationRepository.findAll());
	}
}
