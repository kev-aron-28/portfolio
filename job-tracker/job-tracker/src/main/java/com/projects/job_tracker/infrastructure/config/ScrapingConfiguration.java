package com.projects.job_tracker.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.projects.job_tracker.domain.port.DuplicateJobDetector;
import com.projects.job_tracker.domain.port.JobNormalizer;
import com.projects.job_tracker.domain.port.JobRepository;
import com.projects.job_tracker.domain.scraping.DefaultJobNormalizer;
import com.projects.job_tracker.domain.scraping.SourceUrlDuplicateJobDetector;
import com.projects.job_tracker.infrastructure.scraping.RateLimiter;
import com.projects.job_tracker.infrastructure.scraping.ScrapingSettingsHolder;
import com.projects.job_tracker.infrastructure.scraping.computrabajo.ComputrabajoJobFetcher;
import com.projects.job_tracker.infrastructure.scraping.computrabajo.ComputrabajoJobParser;
import com.projects.job_tracker.infrastructure.scraping.indeed.IndeedJobFetcher;
import com.projects.job_tracker.infrastructure.scraping.indeed.IndeedJobParser;
import com.projects.job_tracker.infrastructure.scraping.linkedin.LinkedInJobFetcher;
import com.projects.job_tracker.infrastructure.scraping.linkedin.LinkedInJobParser;
import com.projects.job_tracker.infrastructure.scraping.occ.OccJobFetcher;
import com.projects.job_tracker.infrastructure.scraping.occ.OccJobParser;

@Configuration
@EnableConfigurationProperties(ScrapingProperties.class)
public class ScrapingConfiguration {

	@Bean
	RateLimiter scrapingRateLimiter(ScrapingSettingsHolder settingsHolder) {
		return new RateLimiter(settingsHolder);
	}

	@Bean
	OccJobFetcher occJobFetcher(
			RateLimiter scrapingRateLimiter,
			ScrapingSettingsHolder settingsHolder,
			OccJobParser occJobParser) {
		return new OccJobFetcher(scrapingRateLimiter, settingsHolder, occJobParser);
	}

	@Bean
	OccJobParser occJobParser() {
		return new OccJobParser();
	}

	@Bean
	LinkedInJobParser linkedInJobParser() {
		return new LinkedInJobParser();
	}

	@Bean
	LinkedInJobFetcher linkedInJobFetcher(
			RateLimiter scrapingRateLimiter,
			ScrapingSettingsHolder settingsHolder,
			LinkedInJobParser linkedInJobParser) {
		return new LinkedInJobFetcher(scrapingRateLimiter, settingsHolder, linkedInJobParser);
	}

	@Bean
	IndeedJobParser indeedJobParser() {
		return new IndeedJobParser();
	}

	@Bean
	IndeedJobFetcher indeedJobFetcher(
			RateLimiter scrapingRateLimiter,
			ScrapingSettingsHolder settingsHolder,
			IndeedJobParser indeedJobParser) {
		return new IndeedJobFetcher(scrapingRateLimiter, settingsHolder, indeedJobParser);
	}

	@Bean
	ComputrabajoJobParser computrabajoJobParser() {
		return new ComputrabajoJobParser();
	}

	@Bean
	ComputrabajoJobFetcher computrabajoJobFetcher(
			RateLimiter scrapingRateLimiter,
			ScrapingSettingsHolder settingsHolder,
			ComputrabajoJobParser computrabajoJobParser) {
		return new ComputrabajoJobFetcher(scrapingRateLimiter, settingsHolder, computrabajoJobParser);
	}

	@Bean
	JobNormalizer jobNormalizer() {
		return new DefaultJobNormalizer();
	}

	@Bean
	DuplicateJobDetector duplicateJobDetector(JobRepository jobRepository) {
		return new SourceUrlDuplicateJobDetector(jobRepository);
	}
}
