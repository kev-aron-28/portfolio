package com.projects.job_tracker.application.profile;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.SearchProfileRepository;

@Service
public class CreateSearchProfileUseCase {

	private final SearchProfileRepository searchProfileRepository;

	public CreateSearchProfileUseCase(SearchProfileRepository searchProfileRepository) {
		this.searchProfileRepository = searchProfileRepository;
	}

	public SearchProfile execute(CreateSearchProfileCommand command) {
		SearchProfile profile = new SearchProfile(null, command.name(), command.keywords(), command.filters());
		return searchProfileRepository.save(profile);
	}

	public record CreateSearchProfileCommand(String name, String keywords, String filters) {
	}
}
