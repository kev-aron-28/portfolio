package com.projects.job_tracker.application.segment;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@Service
public class DeleteMarketSegmentUseCase {

	private final MarketSegmentRepository marketSegmentRepository;

	public DeleteMarketSegmentUseCase(MarketSegmentRepository marketSegmentRepository) {
		this.marketSegmentRepository = marketSegmentRepository;
	}

	public void execute(Long id) {
		marketSegmentRepository.deleteById(id);
	}
}
