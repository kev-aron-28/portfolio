package com.projects.job_tracker.infrastructure.scraping;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class UrlSlugifier {

	private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");

	private UrlSlugifier() {
	}

	public static String slugify(String value) {
		if (value == null || value.isBlank()) {
			return "";
		}
		String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase(Locale.ROOT)
				.trim();
		return NON_ALPHANUMERIC.matcher(normalized).replaceAll("-").replaceAll("^-|-$", "");
	}
}
