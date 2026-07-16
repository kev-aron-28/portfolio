package com.projects.job_tracker.domain.port;

import java.util.List;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapedJob;

public interface JobScraper {

	JobPlatform platform();

	List<ScrapedJob> scrape(ScrapeCriteria criteria);
}
