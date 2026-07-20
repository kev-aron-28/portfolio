package com.projects.job_tracker.application.scraping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.model.ImportedJobSummary;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.model.NormalizedJob;
import com.projects.job_tracker.domain.model.ScrapeCriteria;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.DuplicateJobDetector;
import com.projects.job_tracker.domain.port.JobNormalizer;
import com.projects.job_tracker.domain.port.JobRepository;
import com.projects.job_tracker.domain.port.JobScraper;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;
import com.projects.job_tracker.domain.port.SearchProfileRepository;
import com.projects.job_tracker.infrastructure.scraping.JobScraperRegistry;
import com.projects.job_tracker.infrastructure.scraping.ScrapeFilterSetParser;

@Service
public class ScrapeJobsUseCase {

	private final JobScraperRegistry scraperRegistry;
	private final SearchProfileRepository searchProfileRepository;
	private final MarketSegmentRepository marketSegmentRepository;
	private final JobRepository jobRepository;
	private final JobNormalizer jobNormalizer;
	private final DuplicateJobDetector duplicateJobDetector;
	private final CreateJobUseCase createJobUseCase;

	public ScrapeJobsUseCase(
			JobScraperRegistry scraperRegistry,
			SearchProfileRepository searchProfileRepository,
			MarketSegmentRepository marketSegmentRepository,
			JobRepository jobRepository,
			JobNormalizer jobNormalizer,
			DuplicateJobDetector duplicateJobDetector,
			CreateJobUseCase createJobUseCase) {
		this.scraperRegistry = scraperRegistry;
		this.searchProfileRepository = searchProfileRepository;
		this.marketSegmentRepository = marketSegmentRepository;
		this.jobRepository = jobRepository;
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
							attachExistingJobToSegment(command.segmentId(), normalized);
							continue;
						}
						Job created = createJobUseCase.execute(toCreateCommand(normalized));
						attachJobToSegment(command.segmentId(), created.id());
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

		String keywords = command.keywords();
		ScrapeFilterSet filters = new ScrapeFilterSet(
				command.location(),
				command.salaryMin(),
				command.salaryMax(),
				command.employmentType(),
				command.workMode(),
				command.postedWithinDays());

		if ((keywords == null || keywords.isBlank()) && command.segmentId() != null) {
			MarketSegment segment = marketSegmentRepository
					.findById(command.segmentId())
					.orElseThrow(() -> new ResourceNotFoundException("Segment not found: " + command.segmentId()));
			if (segment.keywords() == null || segment.keywords().isBlank()) {
				throw new IllegalArgumentException("Segment has no keywords configured");
			}
			keywords = segment.keywords();
			if (!hasExplicitFilters(command) && segment.filters() != null && !segment.filters().isBlank()) {
				filters = ScrapeFilterSetParser.parse(segment.filters());
			}
		}

		if (keywords == null || keywords.isBlank()) {
			throw new IllegalArgumentException("keywords, profileId or segment keywords are required");
		}

		return new ScrapeCriteria(keywords, filters, command.maxResults());
	}

	private static boolean hasExplicitFilters(ScrapeJobsCommand command) {
		return (command.location() != null && !command.location().isBlank())
				|| command.salaryMin() != null
				|| command.salaryMax() != null
				|| (command.employmentType() != null && !command.employmentType().isBlank())
				|| (command.workMode() != null && !command.workMode().isBlank())
				|| command.postedWithinDays() != null;
	}

	private void attachExistingJobToSegment(Long segmentId, NormalizedJob normalized) {
		if (segmentId == null) {
			return;
		}
		jobRepository.findBySourceAndUrl(normalized.source(), normalized.url())
				.ifPresent(job -> attachJobToSegment(segmentId, job.id()));
	}

	private void attachJobToSegment(Long segmentId, Long jobId) {
		if (segmentId == null || jobId == null) {
			return;
		}
		marketSegmentRepository.attachJob(segmentId, jobId);
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
			int maxResults,
			Long segmentId) {
	}

	public record ScrapeResult(
			int scraped,
			int imported,
			int duplicates,
			List<String> errors,
			List<ImportedJobSummary> importedJobs) {
	}
}
