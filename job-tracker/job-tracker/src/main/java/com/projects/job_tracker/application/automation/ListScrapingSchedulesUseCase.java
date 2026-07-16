package com.projects.job_tracker.application.automation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.domain.port.ScrapingScheduleRepository;

@Service
public class ListScrapingSchedulesUseCase {

	private final ScrapingScheduleRepository scrapingScheduleRepository;

	public ListScrapingSchedulesUseCase(ScrapingScheduleRepository scrapingScheduleRepository) {
		this.scrapingScheduleRepository = scrapingScheduleRepository;
	}

	public List<ScrapingSchedule> execute() {
		return scrapingScheduleRepository.findAll();
	}
}
