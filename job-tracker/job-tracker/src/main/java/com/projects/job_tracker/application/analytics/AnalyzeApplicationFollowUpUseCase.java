package com.projects.job_tracker.application.analytics;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.FollowUpOverview;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.domain.port.ApplicationRepository;
import com.projects.job_tracker.domain.port.JobReadRepository;

@Service
public class AnalyzeApplicationFollowUpUseCase {

	private final JobReadRepository jobReadRepository;
	private final ApplicationRepository applicationRepository;

	public AnalyzeApplicationFollowUpUseCase(
			JobReadRepository jobReadRepository,
			ApplicationRepository applicationRepository) {
		this.jobReadRepository = jobReadRepository;
		this.applicationRepository = applicationRepository;
	}

	public FollowUpOverview execute() {
		return FollowUpAnalyzer.analyze(
				jobReadRepository.findListings(JobFilterCriteria.empty()),
				applicationRepository.findAll(),
				Instant.now());
	}
}
