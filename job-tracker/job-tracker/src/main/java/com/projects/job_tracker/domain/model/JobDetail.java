package com.projects.job_tracker.domain.model;

import java.util.Optional;

public record JobDetail(
		Job job,
		String companyName,
		String companyWebsite,
		Optional<Application> application) {
}
