package com.projects.job_tracker.domain.port;

import java.util.List;
import java.util.Optional;

import com.projects.job_tracker.domain.model.SearchProfile;

public interface SearchProfileRepository {

	SearchProfile save(SearchProfile profile);

	Optional<SearchProfile> findById(Long id);

	List<SearchProfile> findAll();
}
