package com.projects.job_tracker.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DashboardMetricsTest {

	@Test
	void applicationFunnelUsesPipelineOrderAndRelativeWidth() {
		Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
		byStatus.put(ApplicationStatus.APPLIED, 10L);
		byStatus.put(ApplicationStatus.SCREENING, 5L);
		byStatus.put(ApplicationStatus.INTERVIEWING, 2L);
		byStatus.put(ApplicationStatus.OFFER, 1L);
		byStatus.put(ApplicationStatus.REJECTED, 3L);
		byStatus.put(ApplicationStatus.WITHDRAWN, 1L);

		DashboardMetrics metrics = metrics(22, byStatus);

		assertThat(metrics.applicationFunnel())
				.extracting(ApplicationFunnelStage::status)
				.containsExactly(
						ApplicationStatus.APPLIED,
						ApplicationStatus.SCREENING,
						ApplicationStatus.INTERVIEWING,
						ApplicationStatus.OFFER);
		assertThat(metrics.applicationFunnel())
				.extracting(ApplicationFunnelStage::count)
				.containsExactly(10L, 5L, 2L, 1L);
		assertThat(metrics.applicationFunnel().get(0).widthPercent()).isEqualTo(100);
		assertThat(metrics.applicationFunnel().get(0).displayWidthPercent()).isEqualTo(100);
		assertThat(metrics.applicationFunnel().get(0).nextWidthPercent()).isEqualTo(83);
		assertThat(metrics.applicationFunnel().get(1).widthPercent()).isEqualTo(50);
		assertThat(metrics.applicationFunnel().get(1).displayWidthPercent()).isEqualTo(83);
		assertThat(metrics.applicationFunnel().get(3).displayWidthPercent()).isEqualTo(60);
		assertThat(metrics.applicationFunnel().get(3).nextWidthPercent()).isEqualTo(37);
		assertThat(metrics.applicationFunnel().get(0).sharePercent()).isEqualTo(45);
		assertThat(metrics.applicationFunnel().get(0).conversionPercent()).isEqualTo(100);
		assertThat(metrics.applicationFunnel().get(1).conversionPercent()).isEqualTo(50);
		assertThat(metrics.applicationFunnel().get(2).conversionPercent()).isEqualTo(40);
		assertThat(metrics.applicationFunnel().get(3).conversionPercent()).isEqualTo(50);
		assertThat(metrics.offerCount()).isEqualTo(1);
		assertThat(metrics.offerRatePercent()).isEqualTo(5);
	}

	@Test
	void applicationFunnelIncludesZeroStagesWhenStatusIsMissing() {
		Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
		byStatus.put(ApplicationStatus.APPLIED, 4L);

		DashboardMetrics metrics = metrics(4, byStatus);

		assertThat(metrics.applicationFunnel())
				.extracting(ApplicationFunnelStage::count)
				.containsExactly(4L, 0L, 0L, 0L);
		assertThat(metrics.applicationFunnel().get(1).displayWidthPercent()).isEqualTo(30);
		assertThat(metrics.applicationFunnel().get(1).conversionPercent()).isEqualTo(0);
		assertThat(metrics.applicationOutcomes())
				.extracting(ApplicationFunnelStage::count)
				.containsExactly(0L, 0L);
	}

	@Test
	void emptyApplicationsYieldZeroFunnelShares() {
		DashboardMetrics metrics = metrics(0, new EnumMap<>(ApplicationStatus.class));

		assertThat(metrics.applicationFunnel()).allMatch(stage -> stage.count() == 0 && stage.sharePercent() == 0);
		assertThat(metrics.applicationOutcomes()).allMatch(stage -> stage.count() == 0);
	}

	private static DashboardMetrics metrics(long totalApplications, Map<ApplicationStatus, Long> byStatus) {
		return new DashboardMetrics(
				10, totalApplications, 0, 0, Map.of(), byStatus, MarketInsights.empty(10));
	}
}
