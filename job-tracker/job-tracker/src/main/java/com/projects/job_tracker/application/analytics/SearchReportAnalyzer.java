package com.projects.job_tracker.application.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.projects.job_tracker.domain.model.Application;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.FollowUpOverview;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.ReportFinding;
import com.projects.job_tracker.domain.model.ReportMetric;
import com.projects.job_tracker.domain.model.ReportShare;
import com.projects.job_tracker.domain.model.ReportWait;
import com.projects.job_tracker.domain.model.SearchReport;

public final class SearchReportAnalyzer {

	static final ZoneId ZONE = ZoneId.of("America/Mexico_City");
	private static final DateTimeFormatter WEEK_FORMAT =
			DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es-MX"));

	private SearchReportAnalyzer() {
	}

	public static SearchReport analyze(
			List<JobListing> jobs,
			List<Application> applications,
			FollowUpOverview followUp,
			DashboardMetrics metrics,
			Instant now) {
		Map<Long, Application> byJobId = indexLatest(applications);
		List<JobListing> appliedJobs = jobs.stream()
				.filter(job -> byJobId.containsKey(job.id()))
				.toList();
		if (appliedJobs.isEmpty()) {
			return SearchReport.empty();
		}

		LocalDate today = now.atZone(ZONE).toLocalDate();
		long totalJobs = jobs.size();
		long applied = appliedJobs.size();
		long coverage = percent(applied, totalJobs);
		long progressed = appliedJobs.stream()
				.map(job -> byJobId.get(job.id()).status())
				.filter(status -> status != ApplicationStatus.APPLIED)
				.count();
		long open = followUp.active() + followUp.watch() + followUp.likelySilent();
		long silentShare = percent(followUp.likelySilent(), open);
		Long medianToProgress = medianDaysToProgress(appliedJobs, byJobId);
		long last7 = countAppliedSince(appliedJobs, byJobId, today.minusDays(6));
		long last30 = countAppliedSince(appliedJobs, byJobId, today.minusDays(29));

		List<ReportShare> byStatus = sharesByStatus(appliedJobs, byJobId);
		List<ReportWait> waitByStatus = waitByStatus(appliedJobs, byJobId, today);
		List<ReportShare> weekly = weeklyApplications(appliedJobs, byJobId, today);
		List<ReportShare> bySource = shares(appliedJobs, JobListing::source, "Sin fuente");
		List<ReportShare> byWorkMode = shares(appliedJobs, JobListing::workMode, "Sin modalidad");
		List<ReportShare> byLocation = shares(appliedJobs, JobListing::location, "Sin ubicación");

		List<ReportMetric> snapshot = List.of(
				new ReportMetric("Cobertura", coverage + "%", applied + " de " + totalJobs + " vacantes"),
				new ReportMetric("Avance", progressed + "", progressed == 1 ? "salió de Postulado" : "salieron de Postulado"),
				new ReportMetric("Espera abierta", String.valueOf(open), followUp.likelySilent() + " en probable silencio"),
				new ReportMetric(
						"Respuesta",
						medianToProgress == null ? "—" : daysLabel(medianToProgress),
						medianToProgress == null
								? "sin cambios de estado aún"
								: "mediana hasta el primer movimiento"),
				new ReportMetric("Últimos 7 días", String.valueOf(last7), last30 + " en 30 días"),
				new ReportMetric(
						"Salario postulado",
						money(medianSalary(appliedJobs)),
						"mercado " + money(metrics.market().salary().medianMidpoint())));

		List<ReportFinding> findings = findings(
				totalJobs,
				applied,
				coverage,
				progressed,
				open,
				silentShare,
				followUp,
				last7,
				medianToProgress,
				appliedJobs,
				metrics);

		String headline = headline(applied, totalJobs, coverage, progressed, open, followUp.likelySilent());
		return new SearchReport(
				headline,
				findings,
				snapshot,
				byStatus,
				waitByStatus,
				weekly,
				bySource,
				byWorkMode,
				byLocation,
				true);
	}

	private static String headline(
			long applied,
			long totalJobs,
			long coverage,
			long progressed,
			long open,
			long likelySilent) {
		String base = "Has postulado a " + applied + " de " + totalJobs + " vacantes (" + coverage + "%). ";
		if (progressed == 0) {
			return base + "Ninguna ha avanzado de Postulado; " + open + " siguen abiertas.";
		}
		String moved = progressed == 1
				? "1 ya cambió de estado"
				: progressed + " ya se movieron de estado";
		if (likelySilent > 0) {
			return base + moved + " y " + likelySilent
					+ " llevan tanto tiempo que es posible que no respondan.";
		}
		return base + moved + " y " + open + " siguen en proceso.";
	}

	private static List<ReportFinding> findings(
			long totalJobs,
			long applied,
			long coverage,
			long progressed,
			long open,
			long silentShare,
			FollowUpOverview followUp,
			long last7,
			Long medianToProgress,
			List<JobListing> appliedJobs,
			DashboardMetrics metrics) {
		List<ReportFinding> findings = new ArrayList<>();
		if (silentShare >= 25 && followUp.likelySilent() > 0) {
			findings.add(new ReportFinding(
					"Muchas postulaciones sin movimiento",
					followUp.likelySilent() + " de " + open + " procesos abiertos (" + silentShare
							+ "%) llevan tanto tiempo en el mismo estado que es posible que ya no se comuniquen.",
					"warn"));
		} else if (followUp.watch() > 0) {
			findings.add(new ReportFinding(
					"Hay procesos para dar seguimiento",
					followUp.watch() + (followUp.watch() == 1 ? " postulación lleva " : " postulaciones llevan ")
							+ "más días de lo habitual en su estado. Un follow-up puede ayudar.",
					"warn"));
		}
		if (progressed == 0) {
			findings.add(new ReportFinding(
					"El pipeline aún no avanza",
					"Todas las postulaciones siguen en Postulado. El tiempo de respuesta se está midiendo desde la fecha en que las registraste, no desde una respuesta de la empresa.",
					"info"));
		} else if (medianToProgress != null) {
			findings.add(new ReportFinding(
					"Tiempo hasta el primer movimiento",
					"Cuando una postulación cambia de estado, la mediana es de " + daysLabel(medianToProgress)
							+ ". Eso aproxima qué tan rápido se mueve tu proceso, no una promesa del mercado.",
					"ok"));
		}
		if (last7 == 0) {
			findings.add(new ReportFinding(
					"Semana sin postulaciones nuevas",
					"En los últimos 7 días no registraste postulaciones. El volumen reciente influye más que acumular vacantes antiguas.",
					"info"));
		} else {
			findings.add(new ReportFinding(
					"Ritmo reciente",
					"Registraste " + last7 + (last7 == 1 ? " postulación " : " postulaciones ")
							+ "en los últimos 7 días, sobre " + applied + " en total.",
					"ok"));
		}
		if (coverage < 40 && totalJobs >= 8) {
			findings.add(new ReportFinding(
					"Cobertura baja sobre el mercado importado",
					"Has postulado al " + coverage + "% de las vacantes guardadas. El resto sigue sin estado; puede ser inventario para filtrar, no proceso activo.",
					"info"));
		}
		BigDecimal appliedSalary = medianSalary(appliedJobs);
		BigDecimal marketSalary = metrics.market().salary().medianMidpoint();
		if (appliedSalary != null && marketSalary != null && appliedSalary.compareTo(marketSalary) < 0) {
			findings.add(new ReportFinding(
					"Salario de lo postulado vs mercado",
					"La mediana de las vacantes a las que postulaste (" + money(appliedSalary)
							+ ") está por debajo de la mediana del mercado importado (" + money(marketSalary) + ").",
					"info"));
		} else if (appliedSalary != null && marketSalary != null) {
			findings.add(new ReportFinding(
					"Salario de lo postulado vs mercado",
					"La mediana de las vacantes a las que postulaste es " + money(appliedSalary)
							+ "; el mercado importado está en " + money(marketSalary) + ".",
					"ok"));
		}
		return findings.stream().limit(5).toList();
	}

	private static List<ReportShare> sharesByStatus(List<JobListing> appliedJobs, Map<Long, Application> byJobId) {
		Map<ApplicationStatus, Long> counts = new EnumMap<>(ApplicationStatus.class);
		for (ApplicationStatus status : ApplicationStatus.values()) {
			counts.put(status, 0L);
		}
		for (JobListing job : appliedJobs) {
			ApplicationStatus status = byJobId.get(job.id()).status();
			counts.merge(status, 1L, Long::sum);
		}
		long total = appliedJobs.size();
		List<ReportShare> result = new ArrayList<>();
		for (ApplicationStatus status : ApplicationStatus.values()) {
			long count = counts.getOrDefault(status, 0L);
			if (count > 0) {
				result.add(new ReportShare(status.label(), count, percent(count, total)));
			}
		}
		return result;
	}

	private static List<ReportWait> waitByStatus(
			List<JobListing> appliedJobs,
			Map<Long, Application> byJobId,
			LocalDate today) {
		Map<ApplicationStatus, List<Long>> daysByStatus = new EnumMap<>(ApplicationStatus.class);
		for (JobListing job : appliedJobs) {
			Application application = byJobId.get(job.id());
			if (application.status() == ApplicationStatus.REJECTED || application.status() == ApplicationStatus.WITHDRAWN) {
				continue;
			}
			Instant since = application.statusSince() != null ? application.statusSince() : application.appliedAt();
			if (since == null) {
				continue;
			}
			long days = Math.max(0, ChronoUnit.DAYS.between(since.atZone(ZONE).toLocalDate(), today));
			daysByStatus.computeIfAbsent(application.status(), key -> new ArrayList<>()).add(days);
		}
		List<ReportWait> result = new ArrayList<>();
		for (ApplicationStatus status : ApplicationStatus.pipeline()) {
			List<Long> days = daysByStatus.get(status);
			if (days == null || days.isEmpty()) {
				continue;
			}
			long median = median(days);
			result.add(new ReportWait(status.label(), days.size(), median, daysLabel(median)));
		}
		return result;
	}

	private static Long medianDaysToProgress(List<JobListing> appliedJobs, Map<Long, Application> byJobId) {
		List<Long> days = new ArrayList<>();
		for (JobListing job : appliedJobs) {
			Application application = byJobId.get(job.id());
			if (application.status() == ApplicationStatus.APPLIED) {
				continue;
			}
			if (application.appliedAt() == null || application.statusSince() == null) {
				continue;
			}
			long value = Math.max(0, ChronoUnit.DAYS.between(
					application.appliedAt().atZone(ZONE).toLocalDate(),
					application.statusSince().atZone(ZONE).toLocalDate()));
			days.add(value);
		}
		if (days.isEmpty()) {
			return null;
		}
		return median(days);
	}

	private static List<ReportShare> weeklyApplications(
			List<JobListing> appliedJobs,
			Map<Long, Application> byJobId,
			LocalDate today) {
		LocalDate thisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		Map<LocalDate, Long> counts = new LinkedHashMap<>();
		for (int i = 5; i >= 0; i--) {
			counts.put(thisWeek.minusWeeks(i), 0L);
		}
		for (JobListing job : appliedJobs) {
			Instant appliedAt = byJobId.get(job.id()).appliedAt();
			if (appliedAt == null) {
				continue;
			}
			LocalDate week = appliedAt.atZone(ZONE).toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			if (counts.containsKey(week)) {
				counts.merge(week, 1L, Long::sum);
			}
		}
		long max = counts.values().stream().mapToLong(Long::longValue).max().orElse(0);
		List<ReportShare> result = new ArrayList<>();
		for (Map.Entry<LocalDate, Long> entry : counts.entrySet()) {
			result.add(new ReportShare(
					WEEK_FORMAT.format(entry.getKey()),
					entry.getValue(),
					max == 0 ? 0 : Math.round(entry.getValue() * 100.0 / max)));
		}
		return result;
	}

	private static List<ReportShare> shares(
			List<JobListing> jobs,
			java.util.function.Function<JobListing, String> keyFn,
			String fallback) {
		Map<String, Long> counts = new LinkedHashMap<>();
		for (JobListing job : jobs) {
			String raw = keyFn.apply(job);
			String key = (raw == null || raw.isBlank()) ? fallback : raw.trim();
			counts.merge(key, 1L, Long::sum);
		}
		long total = jobs.size();
		return counts.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
				.limit(6)
				.map(entry -> new ReportShare(entry.getKey(), entry.getValue(), percent(entry.getValue(), total)))
				.toList();
	}

	private static long countAppliedSince(List<JobListing> appliedJobs, Map<Long, Application> byJobId, LocalDate from) {
		return appliedJobs.stream()
				.map(job -> byJobId.get(job.id()).appliedAt())
				.filter(Objects::nonNull)
				.filter(appliedAt -> !appliedAt.atZone(ZONE).toLocalDate().isBefore(from))
				.count();
	}

	private static BigDecimal medianSalary(List<JobListing> jobs) {
		List<BigDecimal> midpoints = jobs.stream()
				.map(SearchReportAnalyzer::midpoint)
				.filter(Objects::nonNull)
				.sorted()
				.toList();
		if (midpoints.isEmpty()) {
			return null;
		}
		int mid = midpoints.size() / 2;
		if (midpoints.size() % 2 == 0) {
			return midpoints.get(mid - 1).add(midpoints.get(mid)).divide(BigDecimal.TWO, 0, RoundingMode.HALF_UP);
		}
		return midpoints.get(mid).setScale(0, RoundingMode.HALF_UP);
	}

	private static BigDecimal midpoint(JobListing job) {
		if (job.salaryMin() != null && job.salaryMax() != null) {
			return job.salaryMin().add(job.salaryMax()).divide(BigDecimal.TWO, 0, RoundingMode.HALF_UP);
		}
		if (job.salaryMin() != null) {
			return job.salaryMin();
		}
		return job.salaryMax();
	}

	private static String money(BigDecimal value) {
		if (value == null) {
			return "—";
		}
		return String.format(Locale.US, "%,.0f MXN", value);
	}

	private static long percent(long part, long total) {
		if (total == 0) {
			return 0;
		}
		return Math.round(part * 100.0 / total);
	}

	private static long median(List<Long> values) {
		List<Long> sorted = values.stream().sorted().toList();
		int mid = sorted.size() / 2;
		if (sorted.size() % 2 == 0) {
			return Math.round((sorted.get(mid - 1) + sorted.get(mid)) / 2.0);
		}
		return sorted.get(mid);
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
