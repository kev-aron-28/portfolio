package com.projects.job_tracker.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.job_tracker.infrastructure.persistence.entity.ScrapingSettingsEntity;

public interface JpaScrapingSettingsRepository extends JpaRepository<ScrapingSettingsEntity, Integer> {
}
