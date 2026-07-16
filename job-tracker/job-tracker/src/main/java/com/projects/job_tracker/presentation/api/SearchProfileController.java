package com.projects.job_tracker.presentation.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.job_tracker.application.profile.CreateSearchProfileUseCase;
import com.projects.job_tracker.application.profile.ListSearchProfilesUseCase;
import com.projects.job_tracker.presentation.api.dto.CreateSearchProfileRequest;
import com.projects.job_tracker.presentation.api.dto.SearchProfileResponse;

@RestController
@RequestMapping("/profiles")
public class SearchProfileController {

	private final CreateSearchProfileUseCase createSearchProfileUseCase;
	private final ListSearchProfilesUseCase listSearchProfilesUseCase;

	public SearchProfileController(
			CreateSearchProfileUseCase createSearchProfileUseCase,
			ListSearchProfilesUseCase listSearchProfilesUseCase) {
		this.createSearchProfileUseCase = createSearchProfileUseCase;
		this.listSearchProfilesUseCase = listSearchProfilesUseCase;
	}

	@GetMapping
	public List<SearchProfileResponse> listProfiles() {
		return listSearchProfilesUseCase.execute().stream().map(SearchProfileResponse::from).toList();
	}

	@PostMapping
	public ResponseEntity<SearchProfileResponse> createProfile(@RequestBody CreateSearchProfileRequest request) {
		var command = new CreateSearchProfileUseCase.CreateSearchProfileCommand(
				request.name(),
				request.keywords(),
				request.filters());

		SearchProfileResponse response = SearchProfileResponse.from(createSearchProfileUseCase.execute(command));
		return ResponseEntity.created(URI.create("/profiles/" + response.id())).body(response);
	}
}
