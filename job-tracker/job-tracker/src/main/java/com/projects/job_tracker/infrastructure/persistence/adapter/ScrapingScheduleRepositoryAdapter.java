package com.projects.job_tracker.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.domain.port.ScrapingScheduleRepository;
import com.projects.job_tracker.infrastructure.persistence.entity.SearchProfileEntity;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaScrapingScheduleRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaSearchProfileRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.ScrapingScheduleMapper;

@Repository
public class ScrapingScheduleRepositoryAdapter implements ScrapingScheduleRepository {

	private final JpaScrapingScheduleRepository jpaScrapingScheduleRepository;
	private final JpaSearchProfileRepository jpaSearchProfileRepository;

	public ScrapingScheduleRepositoryAdapter(
			JpaScrapingScheduleRepository jpaScrapingScheduleRepository,
			JpaSearchProfileRepository jpaSearchProfileRepository) {
		this.jpaScrapingScheduleRepository = jpaScrapingScheduleRepository;
		this.jpaSearchProfileRepository = jpaSearchProfileRepository;
	}

	@Override
	public ScrapingSchedule save(ScrapingSchedule schedule) {
		SearchProfileEntity profile = jpaSearchProfileRepository
				.findById(schedule.profileId())
				.orElseThrow(() -> new IllegalStateException("Profile not found: " + schedule.profileId()));
		return ScrapingScheduleMapper.toDomain(
				jpaScrapingScheduleRepository.save(ScrapingScheduleMapper.toEntity(schedule, profile)));
	}

	@Override
	public Optional<ScrapingSchedule> findById(Long id) {
		return jpaScrapingScheduleRepository.findById(id).map(ScrapingScheduleMapper::toDomain);
	}

	@Override
	public List<ScrapingSchedule> findAll() {
		return jpaScrapingScheduleRepository.findAll().stream().map(ScrapingScheduleMapper::toDomain).toList();
	}

	@Override
	public List<ScrapingSchedule> findAllEnabled() {
		return jpaScrapingScheduleRepository.findByEnabledTrue().stream()
				.map(ScrapingScheduleMapper::toDomain)
				.toList();
	}
}
