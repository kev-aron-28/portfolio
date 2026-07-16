package com.projects.job_tracker.domain.model;

import java.time.Instant;
import java.util.List;

public record JobImportNotification(
		Long profileId,
		String profileName,
		Long scheduleId,
		int importedCount,
		int duplicateCount,
		List<ImportedJobSummary> importedJobs,
		List<String> errors,
		Instant occurredAt) {
}
