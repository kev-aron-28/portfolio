package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.MarketInsights;
import com.projects.job_tracker.domain.port.JobReadRepository;

@ExtendWith(MockitoExtension.class)
class GetDashboardMetricsUseCaseTest {

	@Mock
	private JobReadRepository jobReadRepository;

	@InjectMocks
	private GetDashboardMetricsUseCase getDashboardMetricsUseCase;

	@Test
	void returnsMetricsFromRepository() {
		Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
		byStatus.put(ApplicationStatus.APPLIED, 3L);
		DashboardMetrics metrics = new DashboardMetrics(10, 5, 4, 2, Map.of("occ", 6L, "linkedin", 4L), byStatus, MarketInsights.empty(10));
		when(jobReadRepository.getDashboardMetrics()).thenReturn(metrics);

		DashboardMetrics result = getDashboardMetricsUseCase.execute();

		assertThat(result.totalJobs()).isEqualTo(10);
		assertThat(result.jobsBySource()).containsEntry("occ", 6L);
		assertThat(result.applicationsByStatus()).containsEntry(ApplicationStatus.APPLIED, 3L);
	}
}
