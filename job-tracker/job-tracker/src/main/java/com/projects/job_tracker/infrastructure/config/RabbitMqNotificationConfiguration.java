package com.projects.job_tracker.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "automation.notifications.rabbitmq-enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMqNotificationConfiguration {

	@Bean
	Queue jobImportNotificationQueue(AutomationProperties properties) {
		return new Queue(properties.getNotifications().getQueue(), true);
	}

	@Bean
	DirectExchange jobTrackerNotificationExchange(AutomationProperties properties) {
		return new DirectExchange(properties.getNotifications().getExchange());
	}

	@Bean
	Binding jobImportNotificationBinding(
			Queue jobImportNotificationQueue,
			DirectExchange jobTrackerNotificationExchange,
			AutomationProperties properties) {
		return BindingBuilder.bind(jobImportNotificationQueue)
				.to(jobTrackerNotificationExchange)
				.with(properties.getNotifications().getRoutingKey());
	}

	@Bean
	JacksonJsonMessageConverter jacksonJsonMessageConverter() {
		return new JacksonJsonMessageConverter();
	}
}
