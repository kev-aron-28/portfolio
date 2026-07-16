package com.projects.job_tracker.infrastructure.automation;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.projects.job_tracker.domain.model.JobImportNotification;
import com.projects.job_tracker.domain.port.NotificationPublisher;
import com.projects.job_tracker.infrastructure.config.AutomationProperties;

@Component
@ConditionalOnProperty(name = "automation.notifications.rabbitmq-enabled", havingValue = "true", matchIfMissing = true)
public class RabbitNotificationPublisher implements NotificationPublisher {

	private final RabbitTemplate rabbitTemplate;
	private final AutomationProperties automationProperties;

	public RabbitNotificationPublisher(RabbitTemplate rabbitTemplate, AutomationProperties automationProperties) {
		this.rabbitTemplate = rabbitTemplate;
		this.automationProperties = automationProperties;
	}

	@Override
	public void publish(JobImportNotification notification) {
		rabbitTemplate.convertAndSend(
				automationProperties.getNotifications().getExchange(),
				automationProperties.getNotifications().getRoutingKey(),
				notification);
	}
}
