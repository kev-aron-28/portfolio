package com.projects.job_tracker.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.SearchProfileRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaSearchProfileRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.SearchProfileMapper;

@Repository
public class SearchProfileRepositoryAdapter implements SearchProfileRepository {

	private final JpaSearchProfileRepository jpaSearchProfileRepository;

	public SearchProfileRepositoryAdapter(JpaSearchProfileRepository jpaSearchProfileRepository) {
		this.jpaSearchProfileRepository = jpaSearchProfileRepository;
	}

	@Override
	public SearchProfile save(SearchProfile profile) {
		return SearchProfileMapper.toDomain(
				jpaSearchProfileRepository.save(SearchProfileMapper.toEntity(profile)));
	}

	@Override
	public Optional<SearchProfile> findById(Long id) {
		return jpaSearchProfileRepository.findById(id).map(SearchProfileMapper::toDomain);
	}

	@Override
	public List<SearchProfile> findAll() {
		return jpaSearchProfileRepository.findAll().stream().map(SearchProfileMapper::toDomain).toList();
	}
}
