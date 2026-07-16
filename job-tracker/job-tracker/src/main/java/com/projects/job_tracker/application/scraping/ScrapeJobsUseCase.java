package com.projects.job_tracker.application.scraping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.ImportedJobSummary;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.NormalizedJob;
import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.DuplicateJobDetector;
import com.projects.job_tracker.domain.port.JobNormalizer;
import com.projects.job_tracker.domain.port.JobScraper;
import com.projects.job_tracker.domain.port.SearchProfileRepository;
import com.projects.job_tracker.infrastructure.scraping.JobScraperRegistry;
import com.projects.job_tracker.infrastructure.scraping.ScrapeFilterSetParser;

@Service
public class ScrapeJobsUseCase {

	private final JobScraperRegistry scraperRegistry;
	private final SearchProfileRepository searchProfileRepository;
	private final JobNormalizer jobNormalizer;
	private final DuplicateJobDetector duplicateJobDetector;
	private final CreateJobUseCase createJobUseCase;

	public ScrapeJobsUseCase(
			JobScraperRegistry scraperRegistry,
			SearchProfileRepository searchProfileRepository,
			JobNormalizer jobNormalizer,
			DuplicateJobDetector duplicateJobDetector,
			CreateJobUseCase createJobUseCase) {
		this.scraperRegistry = scraperRegistry;
		this.searchProfileRepository = searchProfileRepository;
		this.jobNormalizer = jobNormalizer;
		this.duplicateJobDetector = duplicateJobDetector;
		this.createJobUseCase = createJobUseCase;
	}

	public ScrapeResult execute(ScrapeJobsCommand command) {
		ScrapeCriteria criteria = buildCriteria(command);
		int scraped = 0;
		int imported = 0;
		int duplicates = 0;
		List<String> errors = new ArrayList<>();
		List<ImportedJobSummary> importedJobs = new ArrayList<>();

		for (JobPlatform platform : command.platforms()) {
			JobScraper scraper = scraperRegistry.get(platform);
			try {
				List<ScrapedJob> jobs = scraper.scrape(criteria);
				scraped += jobs.size();
				for (ScrapedJob scrapedJob : jobs) {
					try {
						NormalizedJob normalized = jobNormalizer.normalize(scrapedJob);
						if (duplicateJobDetector.isDuplicate(normalized)) {
							duplicates++;
							continue;
						}
						createJobUseCase.execute(toCreateCommand(normalized));
						imported++;
						importedJobs.add(new ImportedJobSummary(
								normalized.title(),
								normalized.companyName(),
								normalized.source(),
								normalized.url()));
					} catch (RuntimeException ex) {
						errors.add(platform.source() + " job skipped: " + ex.getMessage());
					}
				}
			} catch (RuntimeException ex) {
				errors.add(platform.source() + " scrape failed: " + ex.getMessage());
			}
		}

		return new ScrapeResult(scraped, imported, duplicates, errors, importedJobs);
	}

	private ScrapeCriteria buildCriteria(ScrapeJobsCommand command) {
		if (command.profileId() != null) {
			SearchProfile profile = searchProfileRepository
					.findById(command.profileId())
					.orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + command.profileId()));
			ScrapeFilterSet filters = ScrapeFilterSetParser.parse(profile.filters());
			return new ScrapeCriteria(profile.keywords(), filters, command.maxResults());
		}

		if (command.keywords() == null || command.keywords().isBlank()) {
			throw new IllegalArgumentException("keywords or profileId is required");
		}

		ScrapeFilterSet filters = new ScrapeFilterSet(
				command.location(),
				command.salaryMin(),
				command.salaryMax(),
				command.employmentType(),
				command.workMode(),
				command.postedWithinDays());
		return new ScrapeCriteria(command.keywords(), filters, command.maxResults());
	}

	private CreateJobUseCase.CreateJobCommand toCreateCommand(NormalizedJob job) {
		return new CreateJobUseCase.CreateJobCommand(
				job.title(),
				job.companyName(),
				job.companyWebsite(),
				job.description(),
				job.location(),
				job.salaryMin(),
				job.salaryMax(),
				job.source(),
				job.url(),
				job.externalId(),
				job.postedAt(),
				job.employmentType(),
				job.workMode(),
				job.category(),
				job.subcategory(),
				job.benefits(),
				job.requirements());
	}

	public record ScrapeJobsCommand(
			Long profileId,
			String keywords,
			String location,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			String employmentType,
			String workMode,
			Integer postedWithinDays,
			List<JobPlatform> platforms,
			int maxResults) {
	}

	public record ScrapeResult(
			int scraped,
			int imported,
			int duplicates,
			List<String> errors,
			List<ImportedJobSummary> importedJobs) {
	}
}
