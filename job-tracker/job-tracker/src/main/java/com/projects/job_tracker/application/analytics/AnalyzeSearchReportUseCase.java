package com.projects.job_tracker.application.analytics;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.FollowUpOverview;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.domain.model.SearchReport;
import com.projects.job_tracker.domain.port.ApplicationRepository;
import com.projects.job_tracker.domain.port.JobReadRepository;

@Service
public class AnalyzeSearchReportUseCase {

	private final JobReadRepository jobReadRepository;
	private final ApplicationRepository applicationRepository;

	public AnalyzeSearchReportUseCase(
			JobReadRepository jobReadRepository,
			ApplicationRepository applicationRepository) {
		this.jobReadRepository = jobReadRepository;
		this.applicationRepository = applicationRepository;
	}

	public SearchReport execute() {
		var jobs = jobReadRepository.findListings(JobFilterCriteria.empty());
		var applications = applicationRepository.findAll();
		Instant now = Instant.now();
		FollowUpOverview followUp = FollowUpAnalyzer.analyze(jobs, applications, now);
		DashboardMetrics metrics = jobReadRepository.getDashboardMetrics();
		return SearchReportAnalyzer.analyze(jobs, applications, followUp, metrics, now);
	}
}
