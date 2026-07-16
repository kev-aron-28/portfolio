package com.projects.job_tracker.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.job_tracker.infrastructure.persistence.entity.ScrapingScheduleEntity;

public interface JpaScrapingScheduleRepository extends JpaRepository<ScrapingScheduleEntity, Long> {

	List<ScrapingScheduleEntity> findByEnabledTrue();
}
