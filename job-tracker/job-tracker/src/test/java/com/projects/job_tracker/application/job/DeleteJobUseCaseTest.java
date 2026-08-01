package com.projects.job_tracker.application.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.domain.exception.ResourceNotFoundException;
import com.projects.job_tracker.domain.port.ApplicationRepository;
import com.projects.job_tracker.domain.port.JobRepository;

import static com.projects.job_tracker.testutil.TestJobs.job;

@ExtendWith(MockitoExtension.class)
class DeleteJobUseCaseTest {

	@Mock
	private JobRepository jobRepository;

	@Mock
	private ApplicationRepository applicationRepository;

	@InjectMocks
	private DeleteJobUseCase deleteJobUseCase;

	@Test
	void deletesApplicationsThenJob() {
		when(jobRepository.findById(5L)).thenReturn(Optional.of(job(
				5L, "Java Dev", 1L, null, "CDMX", null, null, "manual", "https://example.com", Instant.now())));

		deleteJobUseCase.execute(5L);

		verify(applicationRepository).deleteByJobId(5L);
		verify(jobRepository).deleteById(5L);
	}

	@Test
	void deletesMultipleJobsAndDedupesIds() {
		when(jobRepository.findById(1L)).thenReturn(Optional.of(job(
				1L, "A", 1L, null, null, null, null, "manual", "https://example.com/a", Instant.now())));
		when(jobRepository.findById(2L)).thenReturn(Optional.of(job(
				2L, "B", 1L, null, null, null, null, "manual", "https://example.com/b", Instant.now())));

		int deleted = deleteJobUseCase.execute(java.util.Arrays.asList(1L, 2L, 1L, null));

		assertThat(deleted).isEqualTo(2);
		verify(applicationRepository).deleteByJobId(1L);
		verify(applicationRepository).deleteByJobId(2L);
		verify(jobRepository).deleteById(1L);
		verify(jobRepository).deleteById(2L);
	}

	@Test
	void bulkDeleteRequiresAtLeastOneId() {
		assertThatThrownBy(() -> deleteJobUseCase.execute(List.of()))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void throwsWhenJobMissing() {
		when(jobRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> deleteJobUseCase.execute(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("99");

		verify(applicationRepository, never()).deleteByJobId(99L);
		verify(jobRepository, never()).deleteById(99L);
	}
}
