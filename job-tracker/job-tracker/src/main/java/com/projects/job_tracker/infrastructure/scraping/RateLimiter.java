package com.projects.job_tracker.infrastructure.scraping;

public final class RateLimiter {

	private final ScrapingSettingsHolder settingsHolder;
	private long lastRequestAt;

	public RateLimiter(ScrapingSettingsHolder settingsHolder) {
		this.settingsHolder = settingsHolder;
	}

	public void acquire() {
		long delayMs = settingsHolder.rateLimitMs();
		if (delayMs <= 0) {
			return;
		}
		long now = System.currentTimeMillis();
		long elapsed = now - lastRequestAt;
		if (elapsed < delayMs) {
			try {
				Thread.sleep(delayMs - elapsed);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("Rate limit wait interrupted", ex);
			}
		}
		lastRequestAt = System.currentTimeMillis();
	}
}
