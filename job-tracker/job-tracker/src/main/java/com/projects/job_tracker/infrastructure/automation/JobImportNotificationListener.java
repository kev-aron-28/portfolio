package com.projects.job_tracker.infrastructure.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobImportNotification;

@Component
@ConditionalOnProperty(name = "automation.notifications.rabbitmq-enabled", havingValue = "true", matchIfMissing = true)
public class JobImportNotificationListener {

	private static final Logger log = LoggerFactory.getLogger(JobImportNotificationListener.class);

	@RabbitListener(queues = "${automation.notifications.queue}")
	public void handle(JobImportNotification notification) {
		log.info(
				"Received job import notification for profile '{}' ({} new jobs)",
				notification.profileName(),
				notification.importedCount());
	}
}
