package com.projects.job_tracker.infrastructure.persistence.mapper;

import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.infrastructure.persistence.entity.SearchProfileEntity;

public final class SearchProfileMapper {

	private SearchProfileMapper() {
	}

	public static SearchProfile toDomain(SearchProfileEntity entity) {
		return new SearchProfile(entity.getId(), entity.getName(), entity.getKeywords(), entity.getFilters());
	}

	public static SearchProfileEntity toEntity(SearchProfile profile) {
		SearchProfileEntity entity = new SearchProfileEntity();
		entity.setId(profile.id());
		entity.setName(profile.name());
		entity.setKeywords(profile.keywords());
		entity.setFilters(profile.filters());
		return entity;
	}
}
