package com.projects.job_tracker.application.analytics;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobSortField;
import com.projects.job_tracker.domain.model.SortDirection;
import com.projects.job_tracker.domain.port.JobReadRepository;

@Service
public class ListJobListingsUseCase {

	private final JobReadRepository jobReadRepository;

	public ListJobListingsUseCase(JobReadRepository jobReadRepository) {
		this.jobReadRepository = jobReadRepository;
	}

	public List<JobListing> execute(JobListingQuery query) {
		JobFilterCriteria filters = new JobFilterCriteria(
				query.keyword(),
				query.source(),
				query.location(),
				query.companyName(),
				query.minSalary(),
				query.maxSalary(),
				query.workMode(),
				query.employmentType(),
				query.category(),
				query.applicationStatus(),
				query.onlyUnapplied(),
				query.segmentId(),
				query.sortBy(),
				query.sortDirection());
		return jobReadRepository.findListings(filters);
	}

	public record JobListingQuery(
			String keyword,
			String source,
			String location,
			String companyName,
			BigDecimal minSalary,
			BigDecimal maxSalary,
			String workMode,
			String employmentType,
			String category,
			ApplicationStatus applicationStatus,
			boolean onlyUnapplied,
			Long segmentId,
			JobSortField sortBy,
			SortDirection sortDirection) {

		public static JobListingQuery empty() {
			return new JobListingQuery(
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					false,
					null,
					JobSortField.CREATED_AT,
					SortDirection.DESC);
		}
	}
}
