package com.projects.job_tracker.presentation.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.presentation.api.dto.ScrapeJobsRequest;
import com.projects.job_tracker.presentation.api.dto.ScrapeJobsResponse;

@RestController
@RequestMapping("/scraping")
public class ScrapingController {

	private final ScrapeJobsUseCase scrapeJobsUseCase;

	public ScrapingController(ScrapeJobsUseCase scrapeJobsUseCase) {
		this.scrapeJobsUseCase = scrapeJobsUseCase;
	}

	@PostMapping(value = "/run", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ScrapeJobsResponse> runScraping(@RequestBody ScrapeJobsRequest request) {
		ScrapeJobsUseCase.ScrapeResult result = scrapeJobsUseCase.execute(request.toCommand());
		return ResponseEntity.ok(ScrapeJobsResponse.from(result));
	}
}
