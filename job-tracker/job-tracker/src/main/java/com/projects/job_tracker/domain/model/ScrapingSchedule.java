package com.projects.job_tracker.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record ScrapingSchedule(
		Long id,
		Long profileId,
		List<JobPlatform> platforms,
		int intervalMinutes,
		int maxResults,
		boolean enabled,
		Instant lastRunAt,
		Instant createdAt) {

	public ScrapingSchedule {
		if (profileId == null) {
			throw new IllegalArgumentException("Profile id is required");
		}
		if (platforms == null || platforms.isEmpty()) {
			throw new IllegalArgumentException("At least one platform is required");
		}
		if (intervalMinutes <= 0) {
			throw new IllegalArgumentException("intervalMinutes must be positive");
		}
		if (maxResults <= 0) {
			throw new IllegalArgumentException("maxResults must be positive");
		}
	}

	public boolean isDue(Instant now) {
		if (!enabled) {
			return false;
		}
		if (lastRunAt == null) {
			return true;
		}
		return !lastRunAt.plus(intervalMinutes, ChronoUnit.MINUTES).isAfter(now);
	}

	public ScrapingSchedule withLastRunAt(Instant lastRunAt) {
		return new ScrapingSchedule(
				id, profileId, platforms, intervalMinutes, maxResults, enabled, lastRunAt, createdAt);
	}

	public ScrapingSchedule withEnabled(boolean enabled) {
		return new ScrapingSchedule(
				id, profileId, platforms, intervalMinutes, maxResults, enabled, lastRunAt, createdAt);
	}
}
