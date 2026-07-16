package com.projects.job_tracker.infrastructure.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobImportNotification;
import com.projects.job_tracker.domain.port.NotificationPublisher;

@Component
@ConditionalOnProperty(
		name = "automation.notifications.rabbitmq-enabled",
		havingValue = "false",
		matchIfMissing = false)
public class LoggingNotificationPublisher implements NotificationPublisher {

	private static final Logger log = LoggerFactory.getLogger(LoggingNotificationPublisher.class);

	@Override
	public void publish(JobImportNotification notification) {
		log.info(
				"Job import notification: profile={} imported={} duplicates={} jobs={}",
				notification.profileName(),
				notification.importedCount(),
				notification.duplicateCount(),
				notification.importedJobs().size());
	}
}
