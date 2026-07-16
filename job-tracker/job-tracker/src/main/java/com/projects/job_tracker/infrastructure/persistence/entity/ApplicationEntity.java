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
@Table(name = "applications")
public class ApplicationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "job_id", nullable = false)
	private JobEntity job;

	@Column(nullable = false)
	private String status;

	@Column(name = "applied_at")
	private Instant appliedAt;

	@Column(columnDefinition = "TEXT")
	private String notes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JobEntity getJob() {
		return job;
	}

	public void setJob(JobEntity job) {
		this.job = job;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getAppliedAt() {
		return appliedAt;
	}

	public void setAppliedAt(Instant appliedAt) {
		this.appliedAt = appliedAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
