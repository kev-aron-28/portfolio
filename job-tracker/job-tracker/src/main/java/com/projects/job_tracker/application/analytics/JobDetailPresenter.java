package com.projects.job_tracker.application.analytics;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.projects.job_tracker.application.analytics.MarketTextAnalyzer.JobTextFields;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.model.JobDetail;

public final class JobDetailPresenter {

	public record JobDetailView(
			String subtitle,
			String workModeLabel,
			String employmentTypeLabel,
			String salaryLabel,
			String experienceLevel,
			List<String> technologies,
			boolean hasDescription,
			boolean hasRequirements,
			boolean hasBenefits,
			boolean hasContent) {
	}

	private JobDetailPresenter() {
	}

	public static JobDetailView present(JobDetail detail) {
		Job job = detail.job();
		JobTextFields textFields = new JobTextFields(job.title(), job.requirements(), job.description());
		Set<String> technologies = MarketTextAnalyzer.detectTechnologies(textFields);
		List<String> technologyList = technologies.stream()
				.sorted(Comparator.naturalOrder())
				.collect(Collectors.toCollection(ArrayList::new));

		boolean hasDescription = hasText(job.description());
		boolean hasRequirements = hasText(job.requirements());
		boolean hasBenefits = hasText(job.benefits());

		return new JobDetailView(
				buildSubtitle(detail),
				formatWorkMode(job.workMode()),
				formatEmploymentType(job.employmentType()),
				formatSalary(job.salaryMin(), job.salaryMax()),
				MarketTextAnalyzer.detectExperienceLevel(textFields),
				technologyList,
				hasDescription,
				hasRequirements,
				hasBenefits,
				hasDescription || hasRequirements || hasBenefits);
	}

	private static String buildSubtitle(JobDetail detail) {
		Job job = detail.job();
		List<String> parts = new ArrayList<>();
		parts.add(detail.companyName());
		if (hasText(job.location())) {
			parts.add(job.location().trim());
		}
		String workMode = formatWorkMode(job.workMode());
		if (!"Sin modalidad".equals(workMode)) {
			parts.add(workMode);
		}
		return String.join(" · ", parts);
	}

	static String formatWorkMode(String workMode) {
		return MarketInsightsBuilder.formatWorkMode(workMode);
	}

	static String formatEmploymentType(String employmentType) {
		return MarketInsightsBuilder.formatEmploymentType(employmentType);
	}

	static String formatSalary(BigDecimal salaryMin, BigDecimal salaryMax) {
		if (salaryMin == null && salaryMax == null) {
			return null;
		}
		NumberFormat format = NumberFormat.getIntegerInstance(new Locale("es", "MX"));
		if (salaryMin != null && salaryMax != null) {
			return format.format(salaryMin) + " – " + format.format(salaryMax) + " MXN/mes";
		}
		if (salaryMin != null) {
			return "Desde " + format.format(salaryMin) + " MXN/mes";
		}
		return "Hasta " + format.format(salaryMax) + " MXN/mes";
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
