package com.projects.job_tracker.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scraping_settings")
public class ScrapingSettingsEntity {

	@Id
	private Integer id = 1;

	@Column(name = "rate_limit_ms", nullable = false)
	private int rateLimitMs;

	@Column(name = "default_max_results", nullable = false)
	private int defaultMaxResults;

	@Column(name = "default_platforms", nullable = false)
	private String defaultPlatforms;

	@Column(name = "linkedin_storage_state_path")
	private String linkedinStorageStatePath;

	@Column(name = "linkedin_page_timeout_ms", nullable = false)
	private int linkedinPageTimeoutMs;

	@Column(name = "occ_storage_state_path")
	private String occStorageStatePath;

	@Column(name = "occ_page_timeout_ms", nullable = false)
	private int occPageTimeoutMs = 30000;

	@Column(name = "indeed_storage_state_path")
	private String indeedStorageStatePath;

	@Column(name = "indeed_page_timeout_ms", nullable = false)
	private int indeedPageTimeoutMs = 30000;

	@Column(name = "computrabajo_storage_state_path")
	private String computrabajoStorageStatePath;

	@Column(name = "computrabajo_page_timeout_ms", nullable = false)
	private int computrabajoPageTimeoutMs = 30000;

	@Column(name = "scheduling_enabled", nullable = false)
	private boolean schedulingEnabled;

	@Column(name = "scheduling_poll_interval_ms", nullable = false)
	private long schedulingPollIntervalMs;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getRateLimitMs() {
		return rateLimitMs;
	}

	public void setRateLimitMs(int rateLimitMs) {
		this.rateLimitMs = rateLimitMs;
	}

	public int getDefaultMaxResults() {
		return defaultMaxResults;
	}

	public void setDefaultMaxResults(int defaultMaxResults) {
		this.defaultMaxResults = defaultMaxResults;
	}

	public String getDefaultPlatforms() {
		return defaultPlatforms;
	}

	public void setDefaultPlatforms(String defaultPlatforms) {
		this.defaultPlatforms = defaultPlatforms;
	}

	public String getLinkedinStorageStatePath() {
		return linkedinStorageStatePath;
	}

	public void setLinkedinStorageStatePath(String linkedinStorageStatePath) {
		this.linkedinStorageStatePath = linkedinStorageStatePath;
	}

	public int getLinkedinPageTimeoutMs() {
		return linkedinPageTimeoutMs;
	}

	public void setLinkedinPageTimeoutMs(int linkedinPageTimeoutMs) {
		this.linkedinPageTimeoutMs = linkedinPageTimeoutMs;
	}

	public String getOccStorageStatePath() {
		return occStorageStatePath;
	}

	public void setOccStorageStatePath(String occStorageStatePath) {
		this.occStorageStatePath = occStorageStatePath;
	}

	public int getOccPageTimeoutMs() {
		return occPageTimeoutMs;
	}

	public void setOccPageTimeoutMs(int occPageTimeoutMs) {
		this.occPageTimeoutMs = occPageTimeoutMs;
	}

	public String getIndeedStorageStatePath() {
		return indeedStorageStatePath;
	}

	public void setIndeedStorageStatePath(String indeedStorageStatePath) {
		this.indeedStorageStatePath = indeedStorageStatePath;
	}

	public int getIndeedPageTimeoutMs() {
		return indeedPageTimeoutMs;
	}

	public void setIndeedPageTimeoutMs(int indeedPageTimeoutMs) {
		this.indeedPageTimeoutMs = indeedPageTimeoutMs;
	}

	public String getComputrabajoStorageStatePath() {
		return computrabajoStorageStatePath;
	}

	public void setComputrabajoStorageStatePath(String computrabajoStorageStatePath) {
		this.computrabajoStorageStatePath = computrabajoStorageStatePath;
	}

	public int getComputrabajoPageTimeoutMs() {
		return computrabajoPageTimeoutMs;
	}

	public void setComputrabajoPageTimeoutMs(int computrabajoPageTimeoutMs) {
		this.computrabajoPageTimeoutMs = computrabajoPageTimeoutMs;
	}

	public boolean isSchedulingEnabled() {
		return schedulingEnabled;
	}

	public void setSchedulingEnabled(boolean schedulingEnabled) {
		this.schedulingEnabled = schedulingEnabled;
	}

	public long getSchedulingPollIntervalMs() {
		return schedulingPollIntervalMs;
	}

	public void setSchedulingPollIntervalMs(long schedulingPollIntervalMs) {
		this.schedulingPollIntervalMs = schedulingPollIntervalMs;
	}
}
