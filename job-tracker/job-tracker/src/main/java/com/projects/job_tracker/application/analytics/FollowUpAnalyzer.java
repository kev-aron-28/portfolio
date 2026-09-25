package com.projects.job_tracker.application.analytics;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.FollowUpDay;
import com.projects.job_tracker.domain.model.FollowUpEntry;
import com.projects.job_tracker.domain.model.FollowUpOverview;
import com.projects.job_tracker.domain.model.FollowUpSignal;
import com.projects.job_tracker.domain.model.JobListing;

public final class FollowUpAnalyzer {

	static final ZoneId ZONE = ZoneId.of("America/Mexico_City");
	private static final DateTimeFormatter DAY_FORMAT =
			DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("es-MX"));
	private static final Comparator<FollowUpEntry> ENTRY_ORDER = Comparator
			.comparingInt((FollowUpEntry entry) -> signalPriority(entry.signal()))
			.thenComparing(FollowUpEntry::daysInStatus, Comparator.reverseOrder())
			.thenComparing(FollowUpEntry::title, Comparator.nullsLast(String::compareToIgnoreCase));

	private FollowUpAnalyzer() {
	}

	public static FollowUpOverview analyze(
			List<JobListing> jobs,
			List<Application> applications,
			Instant now) {
		Map<Long, Application> byJobId = indexLatestApplication(applications);
		LocalDate today = now.atZone(ZONE).toLocalDate();

		List<FollowUpEntry> entries = new ArrayList<>();
		for (JobListing job : jobs) {
			entries.add(toEntry(job, byJobId.get(job.id()), today));
		}

		long applied = 0;
		long active = 0;
		long watch = 0;
		long likelySilent = 0;
		long closed = 0;
		List<FollowUpEntry> attention = new ArrayList<>();
		for (FollowUpEntry entry : entries) {
			switch (entry.signal()) {
				case ACTIVE -> {
					applied++;
					active++;
				}
				case WATCH -> {
					applied++;
					watch++;
					attention.add(entry);
				}
				case LIKELY_SILENT -> {
					applied++;
					likelySilent++;
					attention.add(entry);
				}
				case CLOSED -> {
					applied++;
					closed++;
				}
				case UNAPPLIED -> {
				}
			}
		}
		attention.sort(ENTRY_ORDER);

		Map<LocalDate, List<FollowUpEntry>> byDay = new LinkedHashMap<>();
		entries.stream()
				.sorted(Comparator.comparing(FollowUpEntry::registeredOn).reversed()
						.thenComparing(ENTRY_ORDER))
				.forEach(entry -> byDay.computeIfAbsent(entry.registeredOn(), key -> new ArrayList<>()).add(entry));

		List<FollowUpDay> days = byDay.entrySet().stream()
				.map(entry -> {
					List<FollowUpEntry> dayEntries = entry.getValue();
					int attentionCount = (int) dayEntries.stream().filter(item -> item.signal().needsAttention()).count();
					return new FollowUpDay(
							entry.getKey(),
							DAY_FORMAT.format(entry.getKey()),
							dayEntries.size(),
							attentionCount,
							dayEntries);
				})
				.toList();

		return new FollowUpOverview(
				jobs.size(),
				applied,
				active,
				watch,
				likelySilent,
				closed,
				List.copyOf(attention),
				days);
	}

	private static FollowUpEntry toEntry(JobListing job, Application application, LocalDate today) {
		LocalDate registeredOn = job.createdAt() == null
				? today
				: job.createdAt().atZone(ZONE).toLocalDate();
		if (application == null) {
			return new FollowUpEntry(
					job.id(),
					job.title(),
					job.companyName(),
					registeredOn,
					job.createdAt(),
					null,
					0,
					"—",
					FollowUpSignal.UNAPPLIED,
					"Aún no hay una postulación registrada.");
		}

		Instant statusSince = application.statusSince() != null ? application.statusSince() : application.appliedAt();
		LocalDate sinceDate = statusSince == null ? registeredOn : statusSince.atZone(ZONE).toLocalDate();
		long days = Math.max(0, ChronoUnit.DAYS.between(sinceDate, today));
		FollowUpSignal signal = signalFor(application.status(), days);
		return new FollowUpEntry(
				job.id(),
				job.title(),
				job.companyName(),
				registeredOn,
				job.createdAt(),
				application.status(),
				days,
				daysLabel(days),
				signal,
				insightFor(application.status(), signal, days));
	}

	static FollowUpSignal signalFor(ApplicationStatus status, long days) {
		if (status == null) {
			return FollowUpSignal.UNAPPLIED;
		}
		if (status == ApplicationStatus.REJECTED || status == ApplicationStatus.WITHDRAWN) {
			return FollowUpSignal.CLOSED;
		}
		int watchAfter = watchAfter(status);
		int silentAfter = silentAfter(status);
		if (days >= silentAfter) {
			return FollowUpSignal.LIKELY_SILENT;
		}
		if (days >= watchAfter) {
			return FollowUpSignal.WATCH;
		}
		return FollowUpSignal.ACTIVE;
	}

	private static int watchAfter(ApplicationStatus status) {
		return switch (status) {
			case APPLIED -> 14;
			case SCREENING -> 10;
			case INTERVIEWING, OFFER -> 7;
			default -> 14;
		};
	}

	private static int silentAfter(ApplicationStatus status) {
		return switch (status) {
			case APPLIED -> 21;
			case SCREENING, INTERVIEWING, OFFER -> 14;
			default -> 21;
		};
	}

	private static String insightFor(ApplicationStatus status, FollowUpSignal signal, long days) {
		return switch (signal) {
			case CLOSED -> "El proceso ya está cerrado. No hay espera de respuesta.";
			case ACTIVE -> "Tiempo habitual en " + status.label() + ". Aún es razonable esperar respuesta.";
			case WATCH -> "Lleva " + daysLabel(days) + " en " + status.label() + " sin movimiento. Conviene un follow-up.";
			case LIKELY_SILENT -> "Lleva " + daysLabel(days) + " en " + status.label()
					+ ". Por el tiempo sin avance, es posible que ya no se comuniquen.";
			case UNAPPLIED -> "Aún no hay una postulación registrada.";
		};
	}

	private static String daysLabel(long days) {
		if (days == 0) {
			return "Hoy";
		}
		if (days == 1) {
			return "1 día";
		}
		return days + " días";
	}

	private static int signalPriority(FollowUpSignal signal) {
		return switch (signal) {
			case LIKELY_SILENT -> 0;
			case WATCH -> 1;
			case ACTIVE -> 2;
			case UNAPPLIED -> 3;
			case CLOSED -> 4;
		};
	}

	private static Map<Long, Application> indexLatestApplication(List<Application> applications) {
		Map<Long, Application> byJobId = new LinkedHashMap<>();
		for (Application application : applications) {
			Application current = byJobId.get(application.jobId());
			if (current == null || isNewer(application, current)) {
				byJobId.put(application.jobId(), application);
			}
		}
		return byJobId;
	}

	private static boolean isNewer(Application candidate, Application current) {
		Instant candidateAt = candidate.appliedAt() == null ? Instant.EPOCH : candidate.appliedAt();
		Instant currentAt = current.appliedAt() == null ? Instant.EPOCH : current.appliedAt();
		return candidateAt.isAfter(currentAt);
	}
}
