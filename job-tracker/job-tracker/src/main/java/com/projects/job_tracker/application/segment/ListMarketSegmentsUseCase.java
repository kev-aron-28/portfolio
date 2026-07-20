package com.projects.job_tracker.application.segment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@Service
public class ListMarketSegmentsUseCase {

	private final MarketSegmentRepository marketSegmentRepository;

	public ListMarketSegmentsUseCase(MarketSegmentRepository marketSegmentRepository) {
		this.marketSegmentRepository = marketSegmentRepository;
	}

	public List<MarketSegment> execute() {
		return marketSegmentRepository.findAll();
	}
}
