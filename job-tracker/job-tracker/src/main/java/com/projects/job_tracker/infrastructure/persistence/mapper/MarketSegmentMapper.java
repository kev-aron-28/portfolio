package com.projects.job_tracker.infrastructure.persistence.mapper;

import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.infrastructure.persistence.entity.MarketSegmentEntity;

public final class MarketSegmentMapper {

	private MarketSegmentMapper() {
	}

	public static MarketSegment toDomain(MarketSegmentEntity entity) {
		return new MarketSegment(
				entity.getId(),
				entity.getName(),
				entity.getDescription(),
				entity.getKeywords(),
				entity.getFilters(),
				entity.getCreatedAt());
	}

	public static MarketSegmentEntity toEntity(MarketSegment segment) {
		MarketSegmentEntity entity = new MarketSegmentEntity();
		entity.setId(segment.id());
		entity.setName(segment.name());
		entity.setDescription(segment.description());
		entity.setKeywords(segment.keywords());
		entity.setFilters(segment.filters());
		entity.setCreatedAt(segment.createdAt());
		return entity;
	}
}
