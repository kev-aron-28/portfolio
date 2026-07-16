package com.projects.job_tracker.application.scraping;

import static com.projects.job_tracker.testutil.TestJobs.job;
import static com.projects.job_tracker.testutil.TestJobs.scrapeCommand;
import static com.projects.job_tracker.testutil.TestJobs.scraped;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.DuplicateJobDetector;
import com.projects.job_tracker.domain.port.JobNormalizer;
import com.projects.job_tracker.domain.port.JobScraper;
import com.projects.job_tracker.domain.port.SearchProfileRepository;
import com.projects.job_tracker.domain.scraping.DefaultJobNormalizer;
import com.projects.job_tracker.infrastructure.scraping.JobScraperRegistry;

@ExtendWith(MockitoExtension.class)
class ScrapeJobsUseCaseTest {

	@Mock
	private JobScraperRegistry scraperRegistry;

	@Mock
	private SearchProfileRepository searchProfileRepository;

	@Mock
	private DuplicateJobDetector duplicateJobDetector;

	@Mock
	private CreateJobUseCase createJobUseCase;

	@Mock
	private JobScraper occScraper;

	private ScrapeJobsUseCase scrapeJobsUseCase;

	@BeforeEach
	void setUp() {
		JobNormalizer normalizer = new DefaultJobNormalizer();
		scrapeJobsUseCase = new ScrapeJobsUseCase(
				scraperRegistry,
				searchProfileRepository,
				normalizer,
				duplicateJobDetector,
				createJobUseCase);
	}

	@Test
	void importsNewJobsAndSkipsDuplicates() {
		SearchProfile profile = new SearchProfile(1L, "Backend", "java", "{\"location\":\"cdmx\"}");
		when(searchProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
		when(scraperRegistry.get(JobPlatform.OCC)).thenReturn(occScraper);
		when(occScraper.scrape(any())).thenReturn(List.of(
				scraped(
						JobPlatform.OCC,
						"1",
						"Java Dev",
						"Acme",
						"CDMX",
						null,
						null,
						"https://www.occ.com.mx/empleos/oferta/java-1"),
				scraped(
						JobPlatform.OCC,
						"2",
						"Spring Dev",
						"Beta",
						"GDL",
						null,
						null,
						"https://www.occ.com.mx/empleos/oferta/spring-2")));
		when(duplicateJobDetector.isDuplicate(any())).thenReturn(false, true);
		when(createJobUseCase.execute(any()))
				.thenReturn(job(
						1L,
						"Java Dev",
						1L,
						null,
						"CDMX",
						null,
						null,
						"occ",
						"https://www.occ.com.mx/empleos/oferta/java-1",
						Instant.now()));

		var result = scrapeJobsUseCase.execute(scrapeCommand(1L, null, null, List.of(JobPlatform.OCC), 10));

		assertThat(result.scraped()).isEqualTo(2);
		assertThat(result.imported()).isEqualTo(1);
		assertThat(result.duplicates()).isEqualTo(1);
		verify(createJobUseCase).execute(any());
	}

	@Test
	void usesKeywordsWhenProfileIsNotProvided() {
		when(scraperRegistry.get(JobPlatform.OCC)).thenReturn(occScraper);
		when(occScraper.scrape(any())).thenReturn(List.of());

		var result = scrapeJobsUseCase.execute(
				scrapeCommand(null, "java developer", "monterrey", List.of(JobPlatform.OCC), 5));

		assertThat(result.scraped()).isZero();
		verify(searchProfileRepository, never()).findById(any());
	}
}
