package com.projects.job_tracker.infrastructure.persistence.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projects.job_tracker.infrastructure.persistence.entity.ApplicationEntity;

public interface JpaApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

	Optional<ApplicationEntity> findFirstByJob_IdOrderByAppliedAtDesc(Long jobId);

	@Query("SELECT a.status, COUNT(a) FROM ApplicationEntity a GROUP BY a.status")
	List<Object[]> countGroupedByStatus();

	@Query("""
			SELECT a.status, COUNT(a) FROM ApplicationEntity a
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = a.job.id AND jm.segmentId = :segmentId)
			GROUP BY a.status""")
	List<Object[]> countGroupedByStatusForSegment(@Param("segmentId") Long segmentId);

	long countByAppliedAtGreaterThanEqual(Instant appliedAt);

	@Query("""
			SELECT COUNT(a) FROM ApplicationEntity a
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = a.job.id AND jm.segmentId = :segmentId)""")
	long countForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT COUNT(a) FROM ApplicationEntity a
			WHERE a.appliedAt >= :appliedAt
			AND EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = a.job.id AND jm.segmentId = :segmentId)""")
	long countByAppliedAtGreaterThanEqualForSegment(
			@Param("appliedAt") Instant appliedAt,
			@Param("segmentId") Long segmentId);
}
