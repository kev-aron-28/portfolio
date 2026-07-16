package com.projects.job_tracker.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "automation")
public class AutomationProperties {

	private Scheduling scheduling = new Scheduling();
	private Notifications notifications = new Notifications();

	public Scheduling getScheduling() {
		return scheduling;
	}

	public void setScheduling(Scheduling scheduling) {
		this.scheduling = scheduling;
	}

	public Notifications getNotifications() {
		return notifications;
	}

	public void setNotifications(Notifications notifications) {
		this.notifications = notifications;
	}

	public static class Scheduling {

		private boolean enabled = true;
		private long pollIntervalMs = 60_000;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public long getPollIntervalMs() {
			return pollIntervalMs;
		}

		public void setPollIntervalMs(long pollIntervalMs) {
			this.pollIntervalMs = pollIntervalMs;
		}
	}

	public static class Notifications {

		private boolean rabbitmqEnabled = true;
		private String exchange = "job-tracker.notifications";
		private String routingKey = "job.imports";
		private String queue = "job-imports";

		public boolean isRabbitmqEnabled() {
			return rabbitmqEnabled;
		}

		public void setRabbitmqEnabled(boolean rabbitmqEnabled) {
			this.rabbitmqEnabled = rabbitmqEnabled;
		}

		public String getExchange() {
			return exchange;
		}

		public void setExchange(String exchange) {
			this.exchange = exchange;
		}

		public String getRoutingKey() {
			return routingKey;
		}

		public void setRoutingKey(String routingKey) {
			this.routingKey = routingKey;
		}

		public String getQueue() {
			return queue;
		}

		public void setQueue(String queue) {
			this.queue = queue;
		}
	}
}
