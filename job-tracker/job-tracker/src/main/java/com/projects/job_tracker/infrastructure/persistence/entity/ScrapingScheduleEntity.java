package com.projects.job_tracker.infrastructure.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "scraping_schedules")
public class ScrapingScheduleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "profile_id", nullable = false)
	private SearchProfileEntity profile;

	@Column(nullable = false)
	private String platforms;

	@Column(name = "interval_minutes", nullable = false)
	private int intervalMinutes;

	@Column(name = "max_results", nullable = false)
	private int maxResults;

	@Column(nullable = false)
	private boolean enabled;

	@Column(name = "last_run_at")
	private Instant lastRunAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SearchProfileEntity getProfile() {
		return profile;
	}

	public void setProfile(SearchProfileEntity profile) {
		this.profile = profile;
	}

	public String getPlatforms() {
		return platforms;
	}

	public void setPlatforms(String platforms) {
		this.platforms = platforms;
	}

	public int getIntervalMinutes() {
		return intervalMinutes;
	}

	public void setIntervalMinutes(int intervalMinutes) {
		this.intervalMinutes = intervalMinutes;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Instant getLastRunAt() {
		return lastRunAt;
	}

	public void setLastRunAt(Instant lastRunAt) {
		this.lastRunAt = lastRunAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
