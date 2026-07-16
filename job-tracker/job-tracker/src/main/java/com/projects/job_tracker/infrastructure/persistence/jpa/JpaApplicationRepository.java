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

	long countByAppliedAtGreaterThanEqual(Instant appliedAt);
}
