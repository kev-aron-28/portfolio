package com.projects.job_tracker.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scraping")
public class ScrapingProperties {

	private long rateLimitMs = 1000;
	private LinkedIn linkedin = new LinkedIn();

	public long getRateLimitMs() {
		return rateLimitMs;
	}

	public void setRateLimitMs(long rateLimitMs) {
		this.rateLimitMs = rateLimitMs;
	}

	public LinkedIn getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(LinkedIn linkedin) {
		this.linkedin = linkedin;
	}

	public static class LinkedIn {

		private String storageStatePath = "";
		private int pageTimeoutMs = 30000;

		public String getStorageStatePath() {
			return storageStatePath;
		}

		public void setStorageStatePath(String storageStatePath) {
			this.storageStatePath = storageStatePath;
		}

		public int getPageTimeoutMs() {
			return pageTimeoutMs;
		}

		public void setPageTimeoutMs(int pageTimeoutMs) {
			this.pageTimeoutMs = pageTimeoutMs;
		}
	}
}
