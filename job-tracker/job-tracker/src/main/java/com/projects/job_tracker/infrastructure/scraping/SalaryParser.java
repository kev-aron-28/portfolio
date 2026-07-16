package com.projects.job_tracker.infrastructure.scraping;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SalaryParser {

	private static final Pattern RANGE_PATTERN = Pattern.compile(
			"\\$?\\s*([\\d,]+(?:\\.\\d+)?)\\s*[-–a]\\s*\\$?\\s*([\\d,]+(?:\\.\\d+)?)",
			Pattern.CASE_INSENSITIVE);

	private SalaryParser() {
	}

	public static BigDecimal[] parseRange(String salaryText) {
		if (salaryText == null || salaryText.isBlank()) {
			return new BigDecimal[] {null, null};
		}
		Matcher matcher = RANGE_PATTERN.matcher(salaryText);
		if (matcher.find()) {
			return new BigDecimal[] {parseAmount(matcher.group(1)), parseAmount(matcher.group(2))};
		}
		Matcher single = Pattern.compile("\\$?\\s*([\\d,]+(?:\\.\\d+)?)").matcher(salaryText);
		if (single.find()) {
			BigDecimal amount = parseAmount(single.group(1));
			return new BigDecimal[] {amount, amount};
		}
		return new BigDecimal[] {null, null};
	}

	private static BigDecimal parseAmount(String raw) {
		String digits = raw.replace(",", "");
		return new BigDecimal(digits);
	}
}
