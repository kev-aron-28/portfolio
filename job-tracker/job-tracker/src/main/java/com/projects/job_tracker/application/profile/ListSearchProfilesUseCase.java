package com.projects.job_tracker.application.profile;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.SearchProfileRepository;

@Service
public class ListSearchProfilesUseCase {

	private final SearchProfileRepository searchProfileRepository;

	public ListSearchProfilesUseCase(SearchProfileRepository searchProfileRepository) {
		this.searchProfileRepository = searchProfileRepository;
	}

	public List<SearchProfile> execute() {
		return searchProfileRepository.findAll();
	}
}
