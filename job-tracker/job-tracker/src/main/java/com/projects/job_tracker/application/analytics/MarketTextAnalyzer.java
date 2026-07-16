package com.projects.job_tracker.application.analytics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.projects.job_tracker.domain.model.RankedCount;

public final class MarketTextAnalyzer {

	private static final Pattern YEARS_PATTERN = Pattern.compile(
			"(\\d+)\\s*\\+?\\s*(?:años?|anos?|years?)",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final List<String> TECHNOLOGIES = List.of(
			"Spring Boot", "Machine Learning", "Data Science", "GitHub Actions", "Next.js", "Node.js",
			"Java", "Python", "JavaScript", "TypeScript", "React", "Angular", "Vue",
			"Spring", "Kotlin", "Go", "Rust", "C#", ".NET", "PHP", "Ruby",
			"SQL", "PostgreSQL", "MySQL", "MongoDB", "Redis", "Elasticsearch", "Kafka",
			"AWS", "Azure", "GCP", "Docker", "Kubernetes", "Terraform", "Git", "Linux",
			"Flutter", "Swift", "Android", "iOS", "GraphQL", "REST", "Microservices",
			"DevOps", "CI/CD", "Jenkins", "Selenium", "Playwright",
			"Power BI", "Tableau", "Spark", "Hadoop",
			"Salesforce", "SAP", "Oracle", "Hibernate", "JPA", "Maven", "Gradle");

	private MarketTextAnalyzer() {
	}

	public record JobTextFields(String title, String requirements, String description) {
	}

	public static Map<String, Long> countExperienceLevels(List<JobTextFields> jobs) {
		Map<String, Long> counts = new LinkedHashMap<>();
		for (JobTextFields job : jobs) {
			String level = detectExperienceLevel(job);
			counts.merge(level, 1L, Long::sum);
		}
		return sortByCountDesc(counts);
	}

	public static Set<String> detectTechnologies(JobTextFields job) {
		String text = combinedText(job);
		Set<String> matchedInJob = new LinkedHashSet<>();
		for (String tech : TECHNOLOGIES) {
			if (!containsTechnology(text, tech)) {
				continue;
			}
			boolean overshadowed = matchedInJob.stream()
					.anyMatch(existing -> existing.length() > tech.length()
							&& existing.toLowerCase(Locale.ROOT).contains(tech.toLowerCase(Locale.ROOT)));
			if (!overshadowed) {
				matchedInJob.removeIf(existing -> tech.length() > existing.length()
						&& tech.toLowerCase(Locale.ROOT).contains(existing.toLowerCase(Locale.ROOT)));
				matchedInJob.add(tech);
			}
		}
		return matchedInJob;
	}

	public static List<RankedCount> topTechnologies(List<JobTextFields> jobs, int limit) {
		Map<String, Long> counts = new LinkedHashMap<>();
		for (JobTextFields job : jobs) {
			for (String tech : detectTechnologies(job)) {
				counts.merge(tech, 1L, Long::sum);
			}
		}
		return toRankedList(counts, limit);
	}

	static String detectExperienceLevel(JobTextFields job) {
		String text = combinedText(job).toLowerCase(Locale.ROOT);

		if (matchesAny(text, "lead", "principal", "architect", "arquitecto", "staff engineer")) {
			return "Lead / Principal";
		}
		if (matchesAny(text, "senior", "sr.", " sr ", "sénior")) {
			return "Senior";
		}
		if (matchesAny(text, "junior", "jr.", " jr ", "trainee", "becario", "practicante", "entry level", "recién egresado", "recien egresado")) {
			return "Junior";
		}
		if (matchesAny(text, "semi-senior", "semisenior", "semi senior", "mid-level", "mid level", "intermedio")) {
			return "Semi-senior";
		}

		Integer years = extractMaxYears(text);
		if (years != null) {
			if (years >= 5) {
				return "Senior (5+ años)";
			}
			if (years >= 2) {
				return "Semi-senior (2-4 años)";
			}
			return "Junior (0-2 años)";
		}

		return "No especificado";
	}

	private static Integer extractMaxYears(String text) {
		Matcher matcher = YEARS_PATTERN.matcher(text);
		int max = -1;
		while (matcher.find()) {
			max = Math.max(max, Integer.parseInt(matcher.group(1)));
		}
		return max >= 0 ? max : null;
	}

	private static boolean matchesAny(String text, String... keywords) {
		for (String keyword : keywords) {
			if (text.contains(keyword)) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsTechnology(String text, String technology) {
		String normalized = text.toLowerCase(Locale.ROOT);
		String needle = technology.toLowerCase(Locale.ROOT);
		if (needle.contains(".")) {
			return normalized.contains(needle);
		}
		int index = normalized.indexOf(needle);
		while (index >= 0) {
			boolean startOk = index == 0 || !Character.isLetterOrDigit(normalized.charAt(index - 1));
			int endIndex = index + needle.length();
			boolean endOk = endIndex >= normalized.length() || !Character.isLetterOrDigit(normalized.charAt(endIndex));
			if (startOk && endOk) {
				return true;
			}
			index = normalized.indexOf(needle, index + 1);
		}
		return false;
	}

	private static String combinedText(JobTextFields job) {
		return joinNonBlank(job.title(), job.requirements(), job.description());
	}

	private static String joinNonBlank(String... parts) {
		StringBuilder builder = new StringBuilder();
		for (String part : parts) {
			if (part != null && !part.isBlank()) {
				if (!builder.isEmpty()) {
					builder.append(' ');
				}
				builder.append(part);
			}
		}
		return builder.toString();
	}

	private static Map<String, Long> sortByCountDesc(Map<String, Long> counts) {
		return counts.entrySet().stream()
				.sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue).reversed()
						.thenComparing(Map.Entry::getKey))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(left, right) -> left,
						LinkedHashMap::new));
	}

	private static List<RankedCount> toRankedList(Map<String, Long> counts, int limit) {
		List<RankedCount> ranked = new ArrayList<>();
		counts.entrySet().stream()
				.sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue).reversed()
						.thenComparing(Map.Entry::getKey))
				.limit(limit)
				.forEach(entry -> ranked.add(new RankedCount(entry.getKey(), entry.getValue())));
		return ranked;
	}
}
