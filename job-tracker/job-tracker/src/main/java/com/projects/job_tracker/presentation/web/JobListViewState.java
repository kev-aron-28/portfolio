package com.projects.job_tracker.presentation.web;

import java.math.BigDecimal;

import org.springframework.web.util.UriComponentsBuilder;

public record JobListViewState(
		String keyword,
		String source,
		String location,
		String companyName,
		BigDecimal minSalary,
		BigDecimal maxSalary,
		String workMode,
		String employmentType,
		String category,
		String applicationStatus,
		Boolean onlyUnapplied,
		Long segmentId,
		String sortBy,
		String sortDirection,
		String groupBy,
		String view) {

	public String redirectToList() {
		return "redirect:" + listPath();
	}

	public String listPath() {
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/jobs");
		add(builder, "keyword", keyword);
		add(builder, "source", source);
		add(builder, "location", location);
		add(builder, "companyName", companyName);
		add(builder, "minSalary", minSalary);
		add(builder, "maxSalary", maxSalary);
		add(builder, "workMode", workMode);
		add(builder, "employmentType", employmentType);
		add(builder, "category", category);
		add(builder, "applicationStatus", applicationStatus);
		if (Boolean.TRUE.equals(onlyUnapplied)) {
			builder.queryParam("onlyUnapplied", "true");
		}
		add(builder, "segmentId", segmentId);
		add(builder, "sortBy", sortBy);
		add(builder, "sortDirection", sortDirection);
		add(builder, "groupBy", groupBy);
		add(builder, "view", view);
		return builder.build().encode().toUriString();
	}

	private static void add(UriComponentsBuilder builder, String name, Object value) {
		if (value == null) {
			return;
		}
		String text = String.valueOf(value).trim();
		if (text.isEmpty()) {
			return;
		}
		builder.queryParam(name, text);
	}
}
