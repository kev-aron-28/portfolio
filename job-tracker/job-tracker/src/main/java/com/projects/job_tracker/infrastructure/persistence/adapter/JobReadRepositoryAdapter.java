package com.projects.job_tracker.infrastructure.persistence.adapter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.projects.job_tracker.application.analytics.MarketInsightsBuilder;
import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.model.JobDetail;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobSortField;
import com.projects.job_tracker.domain.model.SortDirection;
import com.projects.job_tracker.domain.port.JobReadRepository;
import com.projects.job_tracker.infrastructure.persistence.entity.ApplicationEntity;
import com.projects.job_tracker.infrastructure.persistence.entity.JobEntity;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaApplicationRepository;
import com.projects.job_tracker.infrastructure.persistence.jpa.JpaJobRepository;
import com.projects.job_tracker.infrastructure.persistence.mapper.ApplicationMapper;
import com.projects.job_tracker.infrastructure.persistence.mapper.JobMapper;
import com.projects.job_tracker.infrastructure.persistence.specification.JobEntitySpecifications;

@Repository
public class JobReadRepositoryAdapter implements JobReadRepository {

	private final JpaJobRepository jpaJobRepository;
	private final JpaApplicationRepository jpaApplicationRepository;

	public JobReadRepositoryAdapter(JpaJobRepository jpaJobRepository, JpaApplicationRepository jpaApplicationRepository) {
		this.jpaJobRepository = jpaJobRepository;
		this.jpaApplicationRepository = jpaApplicationRepository;
	}

	@Override
	public List<JobListing> findListings(JobFilterCriteria filters) {
		Specification<JobEntity> specification = JobEntitySpecifications.fromCriteria(filters);
		Sort sort = toSort(filters.sortBy(), filters.sortDirection());
		List<JobEntity> jobs = specification == null
				? jpaJobRepository.findAll(sort)
				: jpaJobRepository.findAll(specification, sort);

		return jobs.stream().map(this::toListing).toList();
	}

	@Override
	public Optional<JobDetail> findDetailById(Long id) {
		return jpaJobRepository.findById(id).map(entity -> {
			Job job = JobMapper.toDomain(entity);
			Optional<Application> application = jpaApplicationRepository
					.findFirstByJob_IdOrderByAppliedAtDesc(id)
					.map(ApplicationMapper::toDomain);
			return new JobDetail(
					job,
					entity.getCompany().getName(),
					entity.getCompany().getWebsite(),
					application);
		});
	}

	@Override
	public DashboardMetrics getDashboardMetrics() {
		Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
		long totalJobs = jpaJobRepository.count();
		Map<String, Long> jobsBySource = toStringCountMap(jpaJobRepository.countGroupedBySource());
		Map<ApplicationStatus, Long> applicationsByStatus = toStatusCountMap(jpaApplicationRepository.countGroupedByStatus());
		var market = MarketInsightsBuilder.build(
				totalJobs,
				jpaJobRepository.countDistinctCompanies(),
				jpaJobRepository.countGroupedByWorkMode(),
				jpaJobRepository.countGroupedByCategory(),
				jpaJobRepository.countGroupedByEmploymentType(),
				jpaJobRepository.countGroupedByCompany(),
				jpaJobRepository.countGroupedByLocation(),
				jpaJobRepository.findSalariesWithWorkMode(),
				jpaJobRepository.findTextForMarketAnalysis());

		return new DashboardMetrics(
				totalJobs,
				jpaApplicationRepository.count(),
				jpaJobRepository.countByCreatedAtGreaterThanEqual(weekAgo),
				jpaApplicationRepository.countByAppliedAtGreaterThanEqual(weekAgo),
				jobsBySource,
				applicationsByStatus,
				market);
	}

	private Sort toSort(JobSortField sortBy, SortDirection direction) {
		Sort.Direction sortDirection = direction == SortDirection.ASC
				? Sort.Direction.ASC
				: Sort.Direction.DESC;
		if (sortBy == JobSortField.COMPANY) {
			return Sort.by(sortDirection, "company.name");
		}
		return Sort.by(sortDirection, sortBy.property());
	}

	private JobListing toListing(JobEntity entity) {
		ApplicationStatus status = jpaApplicationRepository
				.findFirstByJob_IdOrderByAppliedAtDesc(entity.getId())
				.map(ApplicationEntity::getStatus)
				.map(ApplicationStatus::valueOf)
				.orElse(null);

		return new JobListing(
				entity.getId(),
				entity.getTitle(),
				entity.getCompany().getName(),
				entity.getLocation(),
				entity.getSource(),
				entity.getSalaryMin(),
				entity.getSalaryMax(),
				entity.getCreatedAt(),
				entity.getPostedAt(),
				entity.getWorkMode(),
				entity.getEmploymentType(),
				entity.getCategory(),
				entity.getRequirements(),
				entity.getDescription(),
				entity.getUrl(),
				status);
	}

	private Map<String, Long> toStringCountMap(List<Object[]> rows) {
		Map<String, Long> result = new HashMap<>();
		for (Object[] row : rows) {
			result.put((String) row[0], (Long) row[1]);
		}
		return result;
	}

	private Map<ApplicationStatus, Long> toStatusCountMap(List<Object[]> rows) {
		Map<ApplicationStatus, Long> result = new EnumMap<>(ApplicationStatus.class);
		for (Object[] row : rows) {
			result.put(ApplicationStatus.valueOf((String) row[0]), (Long) row[1]);
		}
		return result;
	}
}
