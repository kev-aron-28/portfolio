package com.projects.job_tracker.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.job_tracker.infrastructure.persistence.entity.SearchProfileEntity;

public interface JpaSearchProfileRepository extends JpaRepository<SearchProfileEntity, Long> {
}
