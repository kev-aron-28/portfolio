package com.projects.job_tracker.domain.model;

public record ApplicationFunnelStage(
		ApplicationStatus status,
		long count,
		long widthPercent,
		long sharePercent,
		long displayWidthPercent,
		long nextWidthPercent,
		long conversionPercent) {
}
