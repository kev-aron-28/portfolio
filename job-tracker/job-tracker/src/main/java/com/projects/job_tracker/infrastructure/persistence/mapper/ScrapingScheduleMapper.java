package com.projects.job_tracker.infrastructure.persistence.mapper;

import java.util.Arrays;
import java.util.List;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.infrastructure.persistence.entity.ScrapingScheduleEntity;
import com.projects.job_tracker.infrastructure.persistence.entity.SearchProfileEntity;

public final class ScrapingScheduleMapper {

	private static final String PLATFORM_SEPARATOR = ",";

	private ScrapingScheduleMapper() {
	}

	public static ScrapingSchedule toDomain(ScrapingScheduleEntity entity) {
		return new ScrapingSchedule(
				entity.getId(),
				entity.getProfile().getId(),
				parsePlatforms(entity.getPlatforms()),
				entity.getIntervalMinutes(),
				entity.getMaxResults(),
				entity.isEnabled(),
				entity.getLastRunAt(),
				entity.getCreatedAt());
	}

	public static ScrapingScheduleEntity toEntity(ScrapingSchedule schedule, SearchProfileEntity profile) {
		ScrapingScheduleEntity entity = new ScrapingScheduleEntity();
		entity.setId(schedule.id());
		entity.setProfile(profile);
		entity.setPlatforms(serializePlatforms(schedule.platforms()));
		entity.setIntervalMinutes(schedule.intervalMinutes());
		entity.setMaxResults(schedule.maxResults());
		entity.setEnabled(schedule.enabled());
		entity.setLastRunAt(schedule.lastRunAt());
		entity.setCreatedAt(schedule.createdAt());
		return entity;
	}

	private static List<JobPlatform> parsePlatforms(String platforms) {
		return Arrays.stream(platforms.split(PLATFORM_SEPARATOR))
				.map(String::trim)
				.filter(value -> !value.isEmpty())
				.map(JobPlatform::fromSource)
				.toList();
	}

	private static String serializePlatforms(List<JobPlatform> platforms) {
		return platforms.stream().map(JobPlatform::source).reduce((a, b) -> a + PLATFORM_SEPARATOR + b).orElse("");
	}
}
