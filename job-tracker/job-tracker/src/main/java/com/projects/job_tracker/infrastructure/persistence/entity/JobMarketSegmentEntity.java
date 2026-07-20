package com.projects.job_tracker.infrastructure.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_market_segments")
@IdClass(JobMarketSegmentEntity.JobMarketSegmentId.class)
public class JobMarketSegmentEntity {

	@Id
	@Column(name = "job_id", nullable = false)
	private Long jobId;

	@Id
	@Column(name = "segment_id", nullable = false)
	private Long segmentId;

	public JobMarketSegmentEntity() {
	}

	public JobMarketSegmentEntity(Long jobId, Long segmentId) {
		this.jobId = jobId;
		this.segmentId = segmentId;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public Long getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(Long segmentId) {
		this.segmentId = segmentId;
	}

	public static class JobMarketSegmentId implements Serializable {

		private Long jobId;
		private Long segmentId;

		public JobMarketSegmentId() {
		}

		public JobMarketSegmentId(Long jobId, Long segmentId) {
			this.jobId = jobId;
			this.segmentId = segmentId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof JobMarketSegmentId that)) {
				return false;
			}
			return Objects.equals(jobId, that.jobId) && Objects.equals(segmentId, that.segmentId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(jobId, segmentId);
		}
	}
}
