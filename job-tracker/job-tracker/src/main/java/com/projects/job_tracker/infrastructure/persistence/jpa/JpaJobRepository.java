package com.projects.job_tracker.infrastructure.persistence.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projects.job_tracker.infrastructure.persistence.entity.JobEntity;

public interface JpaJobRepository extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {

	Optional<JobEntity> findBySourceAndUrl(String source, String url);

	@Query("SELECT j.source, COUNT(j) FROM JobEntity j GROUP BY j.source")
	List<Object[]> countGroupedBySource();

	@Query("""
			SELECT j.source, COUNT(j) FROM JobEntity j
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)
			GROUP BY j.source""")
	List<Object[]> countGroupedBySourceForSegment(@Param("segmentId") Long segmentId);

	@Query("SELECT j.workMode, COUNT(j) FROM JobEntity j GROUP BY j.workMode")
	List<Object[]> countGroupedByWorkMode();

	@Query("""
			SELECT j.workMode, COUNT(j) FROM JobEntity j
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)
			GROUP BY j.workMode""")
	List<Object[]> countGroupedByWorkModeForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT j.category, COUNT(j) FROM JobEntity j
			WHERE j.category IS NOT NULL AND TRIM(j.category) <> ''
			GROUP BY j.category ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByCategory();

	@Query("""
			SELECT j.category, COUNT(j) FROM JobEntity j
			WHERE j.category IS NOT NULL AND TRIM(j.category) <> ''
			AND EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)
			GROUP BY j.category ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByCategoryForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT j.employmentType, COUNT(j) FROM JobEntity j
			WHERE j.employmentType IS NOT NULL AND TRIM(j.employmentType) <> ''
			GROUP BY j.employmentType ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByEmploymentType();

	@Query("""
			SELECT j.employmentType, COUNT(j) FROM JobEntity j
			WHERE j.employmentType IS NOT NULL AND TRIM(j.employmentType) <> ''
			AND EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)
			GROUP BY j.employmentType ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByEmploymentTypeForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT c.name, COUNT(j) FROM JobEntity j JOIN j.company c
			GROUP BY c.name ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByCompany();

	@Query("""
			SELECT c.name, COUNT(j) FROM JobEntity j JOIN j.company c
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)
			GROUP BY c.name ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByCompanyForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT j.location, COUNT(j) FROM JobEntity j
			WHERE j.location IS NOT NULL AND TRIM(j.location) <> ''
			GROUP BY j.location ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByLocation();

	@Query("""
			SELECT j.location, COUNT(j) FROM JobEntity j
			WHERE j.location IS NOT NULL AND TRIM(j.location) <> ''
			AND EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)
			GROUP BY j.location ORDER BY COUNT(j) DESC""")
	List<Object[]> countGroupedByLocationForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT j.workMode, j.salaryMin, j.salaryMax FROM JobEntity j
			WHERE j.salaryMin IS NOT NULL OR j.salaryMax IS NOT NULL""")
	List<Object[]> findSalariesWithWorkMode();

	@Query("""
			SELECT j.workMode, j.salaryMin, j.salaryMax FROM JobEntity j
			WHERE (j.salaryMin IS NOT NULL OR j.salaryMax IS NOT NULL)
			AND EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)""")
	List<Object[]> findSalariesWithWorkModeForSegment(@Param("segmentId") Long segmentId);

	@Query("SELECT j.title, j.requirements, j.description FROM JobEntity j")
	List<Object[]> findTextForMarketAnalysis();

	@Query("""
			SELECT j.title, j.requirements, j.description FROM JobEntity j
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)""")
	List<Object[]> findTextForMarketAnalysisForSegment(@Param("segmentId") Long segmentId);

	@Query("SELECT COUNT(DISTINCT j.company.id) FROM JobEntity j")
	long countDistinctCompanies();

	@Query("""
			SELECT COUNT(DISTINCT j.company.id) FROM JobEntity j
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)""")
	long countDistinctCompaniesForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT COUNT(j) FROM JobEntity j
			WHERE EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)""")
	long countForSegment(@Param("segmentId") Long segmentId);

	@Query("""
			SELECT COUNT(j) FROM JobEntity j
			WHERE j.createdAt >= :createdAt
			AND EXISTS (
				SELECT 1 FROM JobMarketSegmentEntity jm
				WHERE jm.jobId = j.id AND jm.segmentId = :segmentId)""")
	long countByCreatedAtGreaterThanEqualForSegment(
			@Param("createdAt") Instant createdAt,
			@Param("segmentId") Long segmentId);

	long countByCreatedAtGreaterThanEqual(Instant createdAt);
}
