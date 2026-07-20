package com.projects.job_tracker.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.job_tracker.infrastructure.persistence.entity.JobMarketSegmentEntity;

public interface JpaJobMarketSegmentRepository
		extends JpaRepository<JobMarketSegmentEntity, JobMarketSegmentEntity.JobMarketSegmentId> {

	boolean existsByJobIdAndSegmentId(Long jobId, Long segmentId);

	long countBySegmentId(Long segmentId);
}
