package com.projects.job_tracker.domain.model;

import java.util.List;

public record ScrapingSettings(
		int rateLimitMs,
		int defaultMaxResults,
		List<JobPlatform> defaultPlatforms,
		String linkedinStorageStatePath,
		int linkedinPageTimeoutMs,
		String occStorageStatePath,
		int occPageTimeoutMs,
		String indeedStorageStatePath,
		int indeedPageTimeoutMs,
		String computrabajoStorageStatePath,
		int computrabajoPageTimeoutMs,
		boolean schedulingEnabled,
		long schedulingPollIntervalMs) {

	public ScrapingSettings {
		if (rateLimitMs < 0) {
			throw new IllegalArgumentException("rateLimitMs must be non-negative");
		}
		if (defaultMaxResults <= 0) {
			throw new IllegalArgumentException("defaultMaxResults must be positive");
		}
		if (defaultPlatforms == null || defaultPlatforms.isEmpty()) {
			throw new IllegalArgumentException("defaultPlatforms must not be empty");
		}
		if (linkedinPageTimeoutMs <= 0) {
			throw new IllegalArgumentException("linkedinPageTimeoutMs must be positive");
		}
		if (occPageTimeoutMs <= 0) {
			throw new IllegalArgumentException("occPageTimeoutMs must be positive");
		}
		if (indeedPageTimeoutMs <= 0) {
			throw new IllegalArgumentException("indeedPageTimeoutMs must be positive");
		}
		if (computrabajoPageTimeoutMs <= 0) {
			throw new IllegalArgumentException("computrabajoPageTimeoutMs must be positive");
		}
		if (schedulingPollIntervalMs <= 0) {
			throw new IllegalArgumentException("schedulingPollIntervalMs must be positive");
		}
	}
}
