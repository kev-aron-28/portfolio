package com.projects.job_tracker.domain.port;

import com.projects.job_tracker.domain.model.JobImportNotification;

public interface NotificationPublisher {

	void publish(JobImportNotification notification);
}
