package com.projects.job_tracker.infrastructure.persistence.mapper;

import java.util.Arrays;
import java.util.List;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.infrastructure.persistence.entity.ScrapingSettingsEntity;

public final class ScrapingSettingsMapper {

	private static final String PLATFORM_SEPARATOR = ",";

	private ScrapingSettingsMapper() {
	}

	public static ScrapingSettings toDomain(ScrapingSettingsEntity entity) {
		return new ScrapingSettings(
				entity.getRateLimitMs(),
				entity.getDefaultMaxResults(),
				parsePlatforms(entity.getDefaultPlatforms()),
				entity.getLinkedinStorageStatePath(),
				entity.getLinkedinPageTimeoutMs(),
				entity.getOccStorageStatePath(),
				entity.getOccPageTimeoutMs(),
				entity.getIndeedStorageStatePath(),
				entity.getIndeedPageTimeoutMs(),
				entity.getComputrabajoStorageStatePath(),
				entity.getComputrabajoPageTimeoutMs(),
				entity.isSchedulingEnabled(),
				entity.getSchedulingPollIntervalMs());
	}

	public static ScrapingSettingsEntity toEntity(ScrapingSettings settings) {
		ScrapingSettingsEntity entity = new ScrapingSettingsEntity();
		entity.setId(1);
		entity.setRateLimitMs(settings.rateLimitMs());
		entity.setDefaultMaxResults(settings.defaultMaxResults());
		entity.setDefaultPlatforms(serializePlatforms(settings.defaultPlatforms()));
		entity.setLinkedinStorageStatePath(settings.linkedinStorageStatePath());
		entity.setLinkedinPageTimeoutMs(settings.linkedinPageTimeoutMs());
		entity.setOccStorageStatePath(settings.occStorageStatePath());
		entity.setOccPageTimeoutMs(settings.occPageTimeoutMs());
		entity.setIndeedStorageStatePath(settings.indeedStorageStatePath());
		entity.setIndeedPageTimeoutMs(settings.indeedPageTimeoutMs());
		entity.setComputrabajoStorageStatePath(settings.computrabajoStorageStatePath());
		entity.setComputrabajoPageTimeoutMs(settings.computrabajoPageTimeoutMs());
		entity.setSchedulingEnabled(settings.schedulingEnabled());
		entity.setSchedulingPollIntervalMs(settings.schedulingPollIntervalMs());
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
