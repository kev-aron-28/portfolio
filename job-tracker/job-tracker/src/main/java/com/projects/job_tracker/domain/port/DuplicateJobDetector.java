package com.projects.job_tracker.domain.port;

import com.projects.job_tracker.domain.model.NormalizedJob;

public interface DuplicateJobDetector {

	boolean isDuplicate(NormalizedJob job);
}
