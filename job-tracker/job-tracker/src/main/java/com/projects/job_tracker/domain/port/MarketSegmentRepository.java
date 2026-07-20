package com.projects.job_tracker.domain.port;

import java.util.List;
import java.util.Optional;

import com.projects.job_tracker.domain.model.MarketSegment;

public interface MarketSegmentRepository {

	MarketSegment save(MarketSegment segment);

	Optional<MarketSegment> findById(Long id);

	List<MarketSegment> findAll();

	void deleteById(Long id);

	void attachJob(Long segmentId, Long jobId);

	boolean isJobAttached(Long segmentId, Long jobId);

	long countJobs(Long segmentId);
}
