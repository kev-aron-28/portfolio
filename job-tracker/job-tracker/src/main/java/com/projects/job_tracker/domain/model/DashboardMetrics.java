package com.projects.job_tracker.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record DashboardMetrics(
		long totalJobs,
		long totalApplications,
		long jobsLast7Days,
		long applicationsLast7Days,
		Map<String, Long> jobsBySource,
		Map<ApplicationStatus, Long> applicationsByStatus,
		MarketInsights market) {

	public List<ApplicationFunnelStage> applicationFunnel() {
		return stages(ApplicationStatus.pipeline());
	}

	public List<ApplicationFunnelStage> applicationOutcomes() {
		return stages(ApplicationStatus.outcomes());
	}

	public long offerCount() {
		return countOf(ApplicationStatus.OFFER);
	}

	public long offerRatePercent() {
		return totalApplications == 0 ? 0 : Math.round(offerCount() * 100.0 / totalApplications);
	}

	private List<ApplicationFunnelStage> stages(List<ApplicationStatus> statuses) {
		long max = 0;
		for (ApplicationStatus status : statuses) {
			max = Math.max(max, countOf(status));
		}
		int n = statuses.size();
		long[] counts = new long[n];
		long[] widthPercents = new long[n];
		long[] sharePercents = new long[n];
		long[] displayWidths = new long[n];
		for (int i = 0; i < n; i++) {
			ApplicationStatus status = statuses.get(i);
			long count = countOf(status);
			counts[i] = count;
			widthPercents[i] = max == 0 ? 0 : Math.round(count * 100.0 / max);
			sharePercents[i] = totalApplications == 0 ? 0 : Math.round(count * 100.0 / totalApplications);
			displayWidths[i] = count == 0
					? 30
					: Math.max(42, Math.round(42 + 58.0 * Math.sqrt(count / (double) max)));
		}
		List<ApplicationFunnelStage> result = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			long nextWidth = i + 1 < n
					? displayWidths[i + 1]
					: Math.max(24, Math.round(displayWidths[i] * 0.62));
			long conversionPercent = i == 0 || counts[i - 1] == 0
					? (i == 0 ? 100 : 0)
					: Math.round(counts[i] * 100.0 / counts[i - 1]);
			result.add(new ApplicationFunnelStage(
					statuses.get(i),
					counts[i],
					widthPercents[i],
					sharePercents[i],
					displayWidths[i],
					nextWidth,
					conversionPercent));
		}
		return result;
	}

	private long countOf(ApplicationStatus status) {
		if (applicationsByStatus == null) {
			return 0;
		}
		return applicationsByStatus.getOrDefault(status, 0L);
	}
}
