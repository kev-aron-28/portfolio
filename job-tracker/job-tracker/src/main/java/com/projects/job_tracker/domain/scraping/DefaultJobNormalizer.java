package com.projects.job_tracker.domain.scraping;

import com.projects.job_tracker.domain.model.NormalizedJob;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.domain.port.JobNormalizer;

public final class DefaultJobNormalizer implements JobNormalizer {

	@Override
	public NormalizedJob normalize(ScrapedJob scrapedJob) {
		String companyName = normalizeText(scrapedJob.companyName());
		if (companyName == null) {
			companyName = "Unknown";
		}

		return new NormalizedJob(
				normalizeText(scrapedJob.title()),
				companyName,
				null,
				normalizeOptional(scrapedJob.description()),
				normalizeOptional(scrapedJob.location()),
				scrapedJob.salaryMin(),
				scrapedJob.salaryMax(),
				scrapedJob.platform().source(),
				normalizeUrl(scrapedJob.url()),
				normalizeOptional(scrapedJob.externalId()),
				scrapedJob.postedAt(),
				normalizeOptional(scrapedJob.employmentType()),
				normalizeWorkMode(scrapedJob.workMode()),
				normalizeOptional(scrapedJob.category()),
				normalizeOptional(scrapedJob.subcategory()),
				normalizeOptional(scrapedJob.benefits()),
				normalizeOptional(scrapedJob.requirements()));
	}

	private String normalizeWorkMode(String value) {
		String normalized = normalizeOptional(value);
		if (normalized == null) {
			return null;
		}
		String lower = normalized.toLowerCase();
		if (lower.contains("remoto") || lower.contains("remote") || lower.contains("home office")) {
			return "remote";
		}
		if (lower.contains("hibrid") || lower.contains("hybrid") || lower.contains("mixto")) {
			return "hybrid";
		}
		if (lower.contains("presencial") || lower.contains("oficina") || lower.contains("onsite")) {
			return "onsite";
		}
		return normalized;
	}

	private String normalizeText(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim().replaceAll("\\s+", " ");
		return trimmed.isEmpty() ? null : trimmed;
	}

	private String normalizeOptional(String value) {
		return normalizeText(value);
	}

	private String normalizeUrl(String url) {
		String trimmed = url.trim();
		if (trimmed.startsWith("//")) {
			return "https:" + trimmed;
		}
		if (trimmed.startsWith("/")) {
			throw new IllegalArgumentException("Relative URLs are not allowed: " + trimmed);
		}
		if (trimmed.startsWith("http://")) {
			return "https://" + trimmed.substring("http://".length());
		}
		return trimmed;
	}
}
