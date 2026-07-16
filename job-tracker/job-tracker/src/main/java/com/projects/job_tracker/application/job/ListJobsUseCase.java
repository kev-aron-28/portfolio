package com.projects.job_tracker.application.job;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.port.JobRepository;

@Service
public class ListJobsUseCase {

	private final JobRepository jobRepository;

	public ListJobsUseCase(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	public List<Job> execute() {
		return jobRepository.findAll();
	}
}
