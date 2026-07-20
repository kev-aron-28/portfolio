package com.projects.job_tracker.application.segment;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@Service
public class CreateMarketSegmentUseCase {

	private final MarketSegmentRepository marketSegmentRepository;

	public CreateMarketSegmentUseCase(MarketSegmentRepository marketSegmentRepository) {
		this.marketSegmentRepository = marketSegmentRepository;
	}

	public MarketSegment execute(CreateMarketSegmentCommand command) {
		MarketSegment segment = new MarketSegment(
				null,
				command.name().trim(),
				blankToNull(command.description()),
				blankToNull(command.keywords()),
				blankToNull(command.filters()),
				Instant.now());
		return marketSegmentRepository.save(segment);
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

	public record CreateMarketSegmentCommand(
			String name,
			String description,
			String keywords,
			String filters) {
	}
}
