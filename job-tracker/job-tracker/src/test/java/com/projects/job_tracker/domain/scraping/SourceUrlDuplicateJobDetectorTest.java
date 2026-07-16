package com.projects.job_tracker.domain.scraping;

import static com.projects.job_tracker.testutil.TestJobs.job;
import static com.projects.job_tracker.testutil.TestJobs.scraped;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.port.JobRepository;

@ExtendWith(MockitoExtension.class)
class SourceUrlDuplicateJobDetectorTest {

	@Mock
	private JobRepository jobRepository;

	@Test
	void detectsDuplicateBySourceAndUrl() {
		var detector = new SourceUrlDuplicateJobDetector(jobRepository);
		var scraped = scraped(
				JobPlatform.OCC,
				"1",
				"Java Dev",
				"Acme",
				"CDMX",
				null,
				null,
				"https://www.occ.com.mx/empleos/oferta/java-1");

		var normalized = new DefaultJobNormalizer().normalize(scraped);
		when(jobRepository.findBySourceAndUrl("occ", normalized.url()))
				.thenReturn(Optional.of(job(
						9L,
						"Java Dev",
						1L,
						null,
						"CDMX",
						null,
						null,
						"occ",
						normalized.url(),
						null)));

		assertThat(detector.isDuplicate(normalized)).isTrue();
	}

	@Test
	void returnsFalseWhenJobDoesNotExist() {
		var detector = new SourceUrlDuplicateJobDetector(jobRepository);
		var scraped = scraped(
				JobPlatform.LINKEDIN,
				"99",
				"Backend",
				"Corp",
				null,
				BigDecimal.TEN,
				BigDecimal.TEN,
				"https://www.linkedin.com/jobs/view/99");

		var normalized = new DefaultJobNormalizer().normalize(scraped);
		when(jobRepository.findBySourceAndUrl("linkedin", normalized.url())).thenReturn(Optional.empty());

		assertThat(detector.isDuplicate(normalized)).isFalse();
	}
}
