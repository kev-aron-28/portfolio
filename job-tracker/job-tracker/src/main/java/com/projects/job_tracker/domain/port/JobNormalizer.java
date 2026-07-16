package com.projects.job_tracker.domain.port;

import com.projects.job_tracker.domain.model.NormalizedJob;
import com.projects.job_tracker.domain.model.ScrapedJob;

public interface JobNormalizer {

	NormalizedJob normalize(ScrapedJob scrapedJob);
}
