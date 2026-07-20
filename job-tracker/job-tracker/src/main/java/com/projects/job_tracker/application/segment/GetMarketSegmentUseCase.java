package com.projects.job_tracker.application.segment;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@Service
public class GetMarketSegmentUseCase {

	private final MarketSegmentRepository marketSegmentRepository;

	public GetMarketSegmentUseCase(MarketSegmentRepository marketSegmentRepository) {
		this.marketSegmentRepository = marketSegmentRepository;
	}

	public MarketSegment execute(Long id) {
		return marketSegmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Segment not found: " + id));
	}
}
