package com.projects.job_tracker.infrastructure.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.job_tracker.infrastructure.persistence.entity.MarketSegmentEntity;

public interface JpaMarketSegmentRepository extends JpaRepository<MarketSegmentEntity, Long> {

	List<MarketSegmentEntity> findAllByOrderByNameAsc();
}
