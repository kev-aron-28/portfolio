package com.projects.job_tracker.infrastructure.persistence.adapter;

import org.springframework.stereotype.Repository;

import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.domain.port.ScrapingSettingsRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaScrapingSettingsRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.ScrapingSettingsMapper;

@Repository
public class ScrapingSettingsRepositoryAdapter implements ScrapingSettingsRepository {

	private final JpaScrapingSettingsRepository jpaScrapingSettingsRepository;

	public ScrapingSettingsRepositoryAdapter(JpaScrapingSettingsRepository jpaScrapingSettingsRepository) {
		this.jpaScrapingSettingsRepository = jpaScrapingSettingsRepository;
	}

	@Override
	public ScrapingSettings get() {
		return jpaScrapingSettingsRepository
				.findById(1)
				.map(ScrapingSettingsMapper::toDomain)
				.orElseThrow(() -> new IllegalStateException("Scraping settings not initialized"));
	}

	@Override
	public ScrapingSettings save(ScrapingSettings settings) {
		return ScrapingSettingsMapper.toDomain(
				jpaScrapingSettingsRepository.save(ScrapingSettingsMapper.toEntity(settings)));
	}
}
