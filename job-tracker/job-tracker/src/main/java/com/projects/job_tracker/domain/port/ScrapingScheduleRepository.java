package com.projects.job_tracker.domain.port;

import java.util.List;
import java.util.Optional;

import com.projects.job_tracker.domain.model.ScrapingSchedule;

public interface ScrapingScheduleRepository {

	ScrapingSchedule save(ScrapingSchedule schedule);

	Optional<ScrapingSchedule> findById(Long id);

	List<ScrapingSchedule> findAll();

	List<ScrapingSchedule> findAllEnabled();
}
