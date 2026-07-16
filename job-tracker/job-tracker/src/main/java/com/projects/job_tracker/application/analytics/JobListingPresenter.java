package com.projects.job_tracker.application.analytics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.projects.job_tracker.application.analytics.MarketTextAnalyzer.JobTextFields;

import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.JobGroupField;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobListingGroup;
import com.projects.job_tracker.domain.model.JobListingOverview;

public final class JobListingPresenter {

	private JobListingPresenter() {
	}

	public static JobListingOverview present(List<JobListing> jobs, JobGroupField groupBy) {
		Map<String, Integer> bySource = countBy(jobs, job -> normalize(job.source(), "Sin fuente"));
		Map<String, Integer> byWorkMode = countBy(jobs, job -> formatWorkMode(job.workMode()));

		int unapplied = (int) jobs.stream().filter(job -> job.applicationStatus() == null).count();
		List<JobListingGroup> groups = group(jobs, groupBy);

		return new JobListingOverview(
				jobs.size(),
				unapplied,
				jobs.size() - unapplied,
				bySource,
				byWorkMode,
				groups);
	}

	public static List<JobListingGroup> group(List<JobListing> jobs, JobGroupField groupBy) {
		if (groupBy == JobGroupField.NONE) {
			return List.of(new JobListingGroup("all", "Todas las vacantes", jobs.size(), jobs));
		}
		if (groupBy == JobGroupField.TECHNOLOGY) {
			return groupByTechnology(jobs);
		}

		Map<String, List<JobListing>> grouped = new LinkedHashMap<>();
		for (JobListing job : jobs) {
			String key = groupKey(job, groupBy);
			grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(job);
		}

		return grouped.entrySet().stream()
				.sorted(Comparator
						.<Map.Entry<String, List<JobListing>>>comparingInt(entry -> entry.getValue().size())
						.reversed()
						.thenComparing(entry -> groupLabel(entry.getKey(), groupBy)))
				.map(entry -> new JobListingGroup(
						entry.getKey(),
						groupLabel(entry.getKey(), groupBy),
						entry.getValue().size(),
						List.copyOf(entry.getValue())))
				.toList();
	}

	private static List<JobListingGroup> groupByTechnology(List<JobListing> jobs) {
		Map<String, List<JobListing>> grouped = new LinkedHashMap<>();
		for (JobListing job : jobs) {
			Set<String> technologies = MarketTextAnalyzer.detectTechnologies(toJobTextFields(job));
			if (technologies.isEmpty()) {
				grouped.computeIfAbsent("sin-tecnologia", ignored -> new ArrayList<>()).add(job);
				continue;
			}
			for (String technology : technologies) {
				grouped.computeIfAbsent(technology, ignored -> new ArrayList<>()).add(job);
			}
		}

		return grouped.entrySet().stream()
				.sorted(Comparator
						.<Map.Entry<String, List<JobListing>>>comparingInt(entry -> entry.getValue().size())
						.reversed()
						.thenComparing(entry -> groupLabel(entry.getKey(), JobGroupField.TECHNOLOGY)))
				.map(entry -> new JobListingGroup(
						entry.getKey(),
						groupLabel(entry.getKey(), JobGroupField.TECHNOLOGY),
						entry.getValue().size(),
						List.copyOf(entry.getValue())))
				.toList();
	}

	private static JobTextFields toJobTextFields(JobListing job) {
		return new JobTextFields(job.title(), job.requirements(), job.description());
	}

	private static Map<String, Integer> countBy(List<JobListing> jobs, java.util.function.Function<JobListing, String> keyFn) {
		return jobs.stream()
				.collect(Collectors.groupingBy(keyFn, LinkedHashMap::new, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
	}

	private static String groupKey(JobListing job, JobGroupField groupBy) {
		return switch (groupBy) {
			case SOURCE -> normalize(job.source(), "sin-fuente");
			case LOCATION -> normalize(job.location(), "Sin ubicación");
			case WORK_MODE -> normalize(job.workMode(), "sin-modalidad");
			case APPLICATION_STATUS -> job.applicationStatus() != null
					? job.applicationStatus().name()
					: "sin-postular";
			case CATEGORY -> normalize(job.category(), "Sin categoría");
			case COMPANY -> normalize(job.companyName(), "Sin empresa");
			case TECHNOLOGY -> "all";
			case NONE -> "all";
		};
	}

	private static String groupLabel(String key, JobGroupField groupBy) {
		return switch (groupBy) {
			case SOURCE -> key.equals("sin-fuente") ? "Sin fuente" : key.toLowerCase(Locale.ROOT);
			case LOCATION, CATEGORY, COMPANY -> key;
			case WORK_MODE -> formatWorkMode(key.equals("sin-modalidad") ? null : key);
			case APPLICATION_STATUS -> key.equals("sin-postular")
					? "Sin postular"
					: formatApplicationStatus(key);
			case TECHNOLOGY -> key.equals("sin-tecnologia") ? "Sin tecnología detectada" : key;
			case NONE -> "Todas las vacantes";
		};
	}

	private static String formatWorkMode(String workMode) {
		if (workMode == null || workMode.isBlank() || "sin-modalidad".equals(workMode)) {
			return "Sin modalidad";
		}
		return switch (workMode.toLowerCase(Locale.ROOT)) {
			case "remote", "remoto" -> "Remoto";
			case "hybrid", "hibrido", "híbrido" -> "Híbrido";
			case "onsite", "presencial" -> "Presencial";
			default -> workMode;
		};
	}

	private static String formatApplicationStatus(String status) {
		try {
			return ApplicationStatus.valueOf(status).name();
		}
		catch (IllegalArgumentException ex) {
			return status;
		}
	}

	private static String normalize(String value, String fallback) {
		if (value == null || value.isBlank()) {
			return fallback;
		}
		return value.trim();
	}

	public static String domId(String value) {
		if (value == null || value.isBlank()) {
			return "group";
		}
		return value.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-|-$", "");
	}
}
