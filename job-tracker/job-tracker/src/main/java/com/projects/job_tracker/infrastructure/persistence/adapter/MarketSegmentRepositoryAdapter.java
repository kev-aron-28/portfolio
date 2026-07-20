package com.projects.job_tracker.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;
import com.projects.job_tracker.infrastructure.persistence.entity.JobMarketSegmentEntity;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaJobMarketSegmentRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaMarketSegmentRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.MarketSegmentMapper;

@Repository
public class MarketSegmentRepositoryAdapter implements MarketSegmentRepository {

	private final JpaMarketSegmentRepository jpaMarketSegmentRepository;
	private final JpaJobMarketSegmentRepository jpaJobMarketSegmentRepository;

	public MarketSegmentRepositoryAdapter(
			JpaMarketSegmentRepository jpaMarketSegmentRepository,
			JpaJobMarketSegmentRepository jpaJobMarketSegmentRepository) {
		this.jpaMarketSegmentRepository = jpaMarketSegmentRepository;
		this.jpaJobMarketSegmentRepository = jpaJobMarketSegmentRepository;
	}

	@Override
	public MarketSegment save(MarketSegment segment) {
		return MarketSegmentMapper.toDomain(
				jpaMarketSegmentRepository.save(MarketSegmentMapper.toEntity(segment)));
	}

	@Override
	public Optional<MarketSegment> findById(Long id) {
		return jpaMarketSegmentRepository.findById(id).map(MarketSegmentMapper::toDomain);
	}

	@Override
	public List<MarketSegment> findAll() {
		return jpaMarketSegmentRepository.findAllByOrderByNameAsc().stream()
				.map(MarketSegmentMapper::toDomain)
				.toList();
	}

	@Override
	public void deleteById(Long id) {
		jpaMarketSegmentRepository.deleteById(id);
	}

	@Override
	@Transactional
	public void attachJob(Long segmentId, Long jobId) {
		if (!jpaJobMarketSegmentRepository.existsByJobIdAndSegmentId(jobId, segmentId)) {
			jpaJobMarketSegmentRepository.save(new JobMarketSegmentEntity(jobId, segmentId));
		}
	}

	@Override
	public boolean isJobAttached(Long segmentId, Long jobId) {
		return jpaJobMarketSegmentRepository.existsByJobIdAndSegmentId(jobId, segmentId);
	}

	@Override
	public long countJobs(Long segmentId) {
		return jpaJobMarketSegmentRepository.countBySegmentId(segmentId);
	}
}
