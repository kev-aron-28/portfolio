package com.projects.job_tracker.application.segment;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@Service
public class UpdateMarketSegmentUseCase {

	private final MarketSegmentRepository marketSegmentRepository;

	public UpdateMarketSegmentUseCase(MarketSegmentRepository marketSegmentRepository) {
		this.marketSegmentRepository = marketSegmentRepository;
	}

	public MarketSegment execute(Long id, UpdateMarketSegmentCommand command) {
		MarketSegment existing = marketSegmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Segment not found: " + id));
		MarketSegment updated = new MarketSegment(
				existing.id(),
				command.name().trim(),
				blankToNull(command.description()),
				blankToNull(command.keywords()),
				blankToNull(command.filters()),
				existing.createdAt());
		return marketSegmentRepository.save(updated);
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

	public record UpdateMarketSegmentCommand(
			String name,
			String description,
			String keywords,
			String filters) {
	}
}
