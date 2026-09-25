package com.projects.job_tracker.application.analytics;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationHistory;
import com.projects.job_tracker.domain.model.ApplicationHistoryDay;
import com.projects.job_tracker.domain.model.ApplicationHistoryItem;
import com.projects.job_tracker.domain.model.ApplicationHistoryMonth;
import com.projects.job_tracker.domain.model.JobListing;

public final class ApplicationHistoryAnalyzer {

	static final ZoneId ZONE = ZoneId.of("America/Mexico_City");
	private static final DateTimeFormatter DAY_FORMAT =
			DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es-MX"));
	private static final DateTimeFormatter MONTH_FORMAT =
			DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("es-MX"));

	private ApplicationHistoryAnalyzer() {
	}

	public static ApplicationHistory analyze(
			List<JobListing> jobs,
			List<Application> applications) {
		Map<Long, JobListing> jobsById = new LinkedHashMap<>();
		for (JobListing job : jobs) {
			jobsById.put(job.id(), job);
		}
		Map<Long, Application> latest = indexLatest(applications);

		List<ApplicationHistoryItem> items = new ArrayList<>();
		for (Map.Entry<Long, Application> entry : latest.entrySet()) {
			JobListing job = jobsById.get(entry.getKey());
			Application application = entry.getValue();
			if (job == null || application.appliedAt() == null) {
				continue;
			}
			items.add(new ApplicationHistoryItem(
					job.id(),
					job.title(),
					job.companyName(),
					job.source(),
					application.status(),
					application.appliedAt().atZone(ZONE).toLocalDate(),
					application.appliedAt()));
		}
		items.sort(Comparator
				.comparing(ApplicationHistoryItem::appliedAt).reversed()
				.thenComparing(ApplicationHistoryItem::title, Comparator.nullsLast(String::compareToIgnoreCase)));

		Map<YearMonth, Map<LocalDate, List<ApplicationHistoryItem>>> grouped = new LinkedHashMap<>();
		for (ApplicationHistoryItem item : items) {
			YearMonth month = YearMonth.from(item.appliedOn());
			grouped
					.computeIfAbsent(month, key -> new LinkedHashMap<>())
					.computeIfAbsent(item.appliedOn(), key -> new ArrayList<>())
					.add(item);
		}

		List<ApplicationHistoryMonth> months = grouped.entrySet().stream()
				.sorted(Map.Entry.<YearMonth, Map<LocalDate, List<ApplicationHistoryItem>>>comparingByKey().reversed())
				.map(monthEntry -> {
					List<ApplicationHistoryDay> days = monthEntry.getValue().entrySet().stream()
							.sorted(Map.Entry.<LocalDate, List<ApplicationHistoryItem>>comparingByKey().reversed())
							.map(dayEntry -> new ApplicationHistoryDay(
									dayEntry.getKey(),
									DAY_FORMAT.format(dayEntry.getKey()),
									List.copyOf(dayEntry.getValue())))
							.toList();
					int count = days.stream().mapToInt(day -> day.items().size()).sum();
					return new ApplicationHistoryMonth(
							monthEntry.getKey(),
							capitalize(MONTH_FORMAT.format(monthEntry.getKey())),
							count,
							days);
				})
				.toList();

		return new ApplicationHistory(items.size(), months);
	}

	private static String capitalize(String value) {
		if (value == null || value.isBlank()) {
			return value;
		}
		return Character.toUpperCase(value.charAt(0)) + value.substring(1);
	}

	private static Map<Long, Application> indexLatest(List<Application> applications) {
		Map<Long, Application> byJobId = new LinkedHashMap<>();
		for (Application application : applications) {
			Application current = byJobId.get(application.jobId());
			if (current == null) {
				byJobId.put(application.jobId(), application);
				continue;
			}
			Instant candidate = application.appliedAt() == null ? Instant.EPOCH : application.appliedAt();
			Instant existing = current.appliedAt() == null ? Instant.EPOCH : current.appliedAt();
			if (candidate.isAfter(existing)) {
				byJobId.put(application.jobId(), application);
			}
		}
		return byJobId;
	}
}
