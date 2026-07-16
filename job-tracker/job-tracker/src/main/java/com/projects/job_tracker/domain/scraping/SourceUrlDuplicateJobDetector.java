package com.projects.job_tracker.domain.scraping;

import com.projects.job_tracker.domain.model.NormalizedJob;
import com.projects.job_tracker.domain.port.DuplicateJobDetector;
import com.projects.job_tracker.domain.port.JobRepository;

public final class SourceUrlDuplicateJobDetector implements DuplicateJobDetector {

	private final JobRepository jobRepository;

	public SourceUrlDuplicateJobDetector(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Override
	public boolean isDuplicate(NormalizedJob job) {
		return jobRepository.findBySourceAndUrl(job.source(), job.url()).isPresent();
	}
}
