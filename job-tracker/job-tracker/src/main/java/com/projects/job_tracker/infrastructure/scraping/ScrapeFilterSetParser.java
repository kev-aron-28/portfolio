package com.projects.job_tracker.infrastructure.scraping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.projects.job_tracker.domain.model.ScrapeFilterSet;

public final class ScrapeFilterSetParser {

	private static final Pattern STRING_FIELD =
			Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"]*)\"");
	private static final Pattern NUMBER_FIELD =
			Pattern.compile("\"(\\w+)\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");

	private ScrapeFilterSetParser() {
	}

	public static ScrapeFilterSet parse(String filtersJson) {
		if (filtersJson == null || filtersJson.isBlank()) {
			return ScrapeFilterSet.empty();
		}
		return new ScrapeFilterSet(
				stringField(filtersJson, "location"),
				decimalField(filtersJson, "salaryMin"),
				decimalField(filtersJson, "salaryMax"),
				stringField(filtersJson, "employmentType"),
				stringField(filtersJson, "workMode"),
				intField(filtersJson, "postedWithinDays"));
	}

	public static String toJson(ScrapeFilterSet filters) {
		if (filters == null || filters.isEmpty()) {
			return null;
		}
		List<String> parts = new ArrayList<>();
		addString(parts, "location", filters.location());
		addDecimal(parts, "salaryMin", filters.salaryMin());
		addDecimal(parts, "salaryMax", filters.salaryMax());
		addString(parts, "employmentType", filters.employmentType());
		addString(parts, "workMode", filters.workMode());
		addInteger(parts, "postedWithinDays", filters.postedWithinDays());
		return "{" + String.join(",", parts) + "}";
	}

	private static void addString(List<String> parts, String key, String value) {
		if (value != null && !value.isBlank()) {
			parts.add("\"" + key + "\":\"" + escape(value) + "\"");
		}
	}

	private static void addDecimal(List<String> parts, String key, BigDecimal value) {
		if (value != null) {
			parts.add("\"" + key + "\":" + value.toPlainString());
		}
	}

	private static void addInteger(List<String> parts, String key, Integer value) {
		if (value != null) {
			parts.add("\"" + key + "\":" + value);
		}
	}

	private static String escape(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static String stringField(String json, String field) {
		Matcher matcher = STRING_FIELD.matcher(json);
		while (matcher.find()) {
			if (field.equals(matcher.group(1))) {
				return matcher.group(2);
			}
		}
		return null;
	}

	private static BigDecimal decimalField(String json, String field) {
		Matcher matcher = NUMBER_FIELD.matcher(json);
		while (matcher.find()) {
			if (field.equals(matcher.group(1))) {
				return new BigDecimal(matcher.group(2));
			}
		}
		return null;
	}

	private static Integer intField(String json, String field) {
		BigDecimal value = decimalField(json, field);
		return value != null ? value.intValue() : null;
	}
}
