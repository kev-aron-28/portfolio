package com.projects.job_tracker.presentation.api.dto;

import com.projects.job_tracker.domain.model.SearchProfile;

public record SearchProfileResponse(Long id, String name, String keywords, String filters) {

	public static SearchProfileResponse from(SearchProfile profile) {
		return new SearchProfileResponse(profile.id(), profile.name(), profile.keywords(), profile.filters());
	}
}
