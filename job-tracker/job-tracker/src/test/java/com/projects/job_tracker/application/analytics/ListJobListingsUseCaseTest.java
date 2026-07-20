package com.projects.job_tracker.application.analytics;

import static com.projects.job_tracker.testutil.TestJobs.listing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobSortField;
import com.projects.job_tracker.domain.model.SortDirection;
import com.projects.job_tracker.domain.port.JobReadRepository;

@ExtendWith(MockitoExtension.class)
class ListJobListingsUseCaseTest {

	@Mock
	private JobReadRepository jobReadRepository;

	@InjectMocks
	private ListJobListingsUseCase listJobListingsUseCase;

	@Test
	void passesFiltersToRepository() {
		var jobListing = listing(
				1L,
				"Java Dev",
				"Acme",
				"CDMX",
				"occ",
				new BigDecimal("50000"),
				new BigDecimal("80000"),
				Instant.now(),
				"https://example.com",
				ApplicationStatus.APPLIED);
		when(jobReadRepository.findListings(any())).thenReturn(List.of(jobListing));

		var query = new ListJobListingsUseCase.JobListingQuery(
				"java",
				"occ",
				"cdmx",
				null,
				new BigDecimal("40000"),
				null,
				null,
				null,
				null,
				ApplicationStatus.APPLIED,
				false,
				null,
				JobSortField.CREATED_AT,
				SortDirection.DESC);
		List<JobListing> result = listJobListingsUseCase.execute(query);

		assertThat(result).hasSize(1);
		ArgumentCaptor<JobFilterCriteria> captor = ArgumentCaptor.forClass(JobFilterCriteria.class);
		verify(jobReadRepository).findListings(captor.capture());
		assertThat(captor.getValue().keyword()).isEqualTo("java");
		assertThat(captor.getValue().source()).isEqualTo("occ");
	}
}
