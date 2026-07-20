package com.projects.job_tracker.infrastructure.persistence.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.JobFilterCriteria;
import com.projects.job_tracker.infrastructure.persistence.entity.ApplicationEntity;
import com.projects.job_tracker.infrastructure.persistence.entity.JobEntity;
import com.projects.job_tracker.infrastructure.persistence.entity.JobMarketSegmentEntity;

import jakarta.persistence.criteria.Subquery;

public final class JobEntitySpecifications {

	private JobEntitySpecifications() {
	}

	public static Specification<JobEntity> fromCriteria(JobFilterCriteria filters) {
		List<Specification<JobEntity>> specs = new ArrayList<>();
		addIfPresent(specs, keywordSpec(filters.keyword()));
		addIfPresent(specs, sourceSpec(filters.source()));
		addIfPresent(specs, locationSpec(filters.location()));
		addIfPresent(specs, companyNameSpec(filters.companyName()));
		addIfPresent(specs, minSalarySpec(filters.minSalary()));
		addIfPresent(specs, maxSalarySpec(filters.maxSalary()));
		addIfPresent(specs, workModeSpec(filters.workMode()));
		addIfPresent(specs, employmentTypeSpec(filters.employmentType()));
		addIfPresent(specs, categorySpec(filters.category()));
		addIfPresent(specs, applicationStatusSpec(filters.applicationStatus()));
		addIfPresent(specs, onlyUnappliedSpec(filters.onlyUnapplied()));
		addIfPresent(specs, segmentSpec(filters.segmentId()));
		if (specs.isEmpty()) {
			return null;
		}
		return Specification.allOf(specs);
	}

	private static void addIfPresent(List<Specification<JobEntity>> specs, Specification<JobEntity> spec) {
		if (spec != null) {
			specs.add(spec);
		}
	}

	private static Specification<JobEntity> keywordSpec(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		String pattern = "%" + keyword.trim().toLowerCase() + "%";
		return (root, query, builder) -> builder.or(
				builder.like(builder.lower(root.get("title")), pattern),
				builder.like(builder.lower(root.get("description")), pattern),
				builder.like(builder.lower(root.get("requirements")), pattern),
				builder.like(builder.lower(root.get("benefits")), pattern),
				builder.like(builder.lower(root.get("category")), pattern),
				builder.like(builder.lower(root.join("company").get("name")), pattern));
	}

	private static Specification<JobEntity> sourceSpec(String source) {
		if (source == null || source.isBlank()) {
			return null;
		}
		return (root, query, builder) -> builder.equal(builder.lower(root.get("source")), source.trim().toLowerCase());
	}

	private static Specification<JobEntity> locationSpec(String location) {
		if (location == null || location.isBlank()) {
			return null;
		}
		String pattern = "%" + location.trim().toLowerCase() + "%";
		return (root, query, builder) -> builder.like(builder.lower(root.get("location")), pattern);
	}

	private static Specification<JobEntity> companyNameSpec(String companyName) {
		if (companyName == null || companyName.isBlank()) {
			return null;
		}
		String pattern = "%" + companyName.trim().toLowerCase() + "%";
		return (root, query, builder) ->
				builder.like(builder.lower(root.join("company").get("name")), pattern);
	}

	private static Specification<JobEntity> minSalarySpec(BigDecimal minSalary) {
		if (minSalary == null) {
			return null;
		}
		return (root, query, builder) -> builder.or(
				builder.greaterThanOrEqualTo(root.get("salaryMax"), minSalary),
				builder.greaterThanOrEqualTo(root.get("salaryMin"), minSalary));
	}

	private static Specification<JobEntity> maxSalarySpec(BigDecimal maxSalary) {
		if (maxSalary == null) {
			return null;
		}
		return (root, query, builder) -> builder.or(
				builder.lessThanOrEqualTo(root.get("salaryMin"), maxSalary),
				builder.lessThanOrEqualTo(root.get("salaryMax"), maxSalary));
	}

	private static Specification<JobEntity> workModeSpec(String workMode) {
		if (workMode == null || workMode.isBlank()) {
			return null;
		}
		String pattern = "%" + workMode.trim().toLowerCase() + "%";
		return (root, query, builder) -> builder.like(builder.lower(root.get("workMode")), pattern);
	}

	private static Specification<JobEntity> employmentTypeSpec(String employmentType) {
		if (employmentType == null || employmentType.isBlank()) {
			return null;
		}
		String pattern = "%" + employmentType.trim().toLowerCase() + "%";
		return (root, query, builder) -> builder.like(builder.lower(root.get("employmentType")), pattern);
	}

	private static Specification<JobEntity> categorySpec(String category) {
		if (category == null || category.isBlank()) {
			return null;
		}
		String pattern = "%" + category.trim().toLowerCase() + "%";
		return (root, query, builder) -> builder.or(
				builder.like(builder.lower(root.get("category")), pattern),
				builder.like(builder.lower(root.get("subcategory")), pattern));
	}

	private static Specification<JobEntity> applicationStatusSpec(ApplicationStatus status) {
		if (status == null) {
			return null;
		}
		return (root, query, builder) -> {
			Subquery<Long> subquery = query.subquery(Long.class);
			var applicationRoot = subquery.from(ApplicationEntity.class);
			subquery.select(applicationRoot.get("job").get("id"));
			subquery.where(builder.equal(applicationRoot.get("status"), status.name()));
			return root.get("id").in(subquery);
		};
	}

	private static Specification<JobEntity> onlyUnappliedSpec(boolean onlyUnapplied) {
		if (!onlyUnapplied) {
			return null;
		}
		return (root, query, builder) -> {
			Subquery<Long> subquery = query.subquery(Long.class);
			var applicationRoot = subquery.from(ApplicationEntity.class);
			subquery.select(applicationRoot.get("job").get("id"));
			return builder.not(root.get("id").in(subquery));
		};
	}

	private static Specification<JobEntity> segmentSpec(Long segmentId) {
		if (segmentId == null) {
			return null;
		}
		return (root, query, builder) -> {
			Subquery<Long> subquery = query.subquery(Long.class);
			var segmentRoot = subquery.from(JobMarketSegmentEntity.class);
			subquery.select(segmentRoot.get("jobId"));
			subquery.where(builder.equal(segmentRoot.get("segmentId"), segmentId));
			return root.get("id").in(subquery);
		};
	}
}
