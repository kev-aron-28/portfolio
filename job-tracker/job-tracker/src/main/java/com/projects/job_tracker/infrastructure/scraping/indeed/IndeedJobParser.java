package com.projects.job_tracker.infrastructure.scraping.indeed;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.infrastructure.scraping.SalaryParser;

public class IndeedJobParser {

	private static final String BASE_URL = "https://mx.indeed.com";
	private static final Pattern JOB_KEY_PATTERN = Pattern.compile("[?&]jk=([a-zA-Z0-9]+)");

	public List<ScrapedJob> parse(String html, int maxResults) {
		Document document = Jsoup.parse(html, BASE_URL);
		List<ScrapedJob> jobs = new ArrayList<>();
		Set<String> seenKeys = new LinkedHashSet<>();

		Elements cards = document.select(
				"#mosaic-jobResults div.job_seen_beacon, "
						+ ".mosaic-provider-jobcards div.job_seen_beacon, "
						+ "div.cardOutline.result .job_seen_beacon");
		if (cards.isEmpty()) {
			cards = document.select("div.job_seen_beacon");
		}

		for (Element card : cards) {
			if (isHiddenCard(card)) {
				continue;
			}
			ScrapedJob job = parseCard(card);
			if (job != null && seenKeys.add(job.externalId())) {
				jobs.add(job);
				if (jobs.size() >= maxResults) {
					break;
				}
			}
		}
		return jobs;
	}

	public Optional<ScrapedJob> enrichWithDetail(ScrapedJob summary, String detailHtml) {
		Document document = Jsoup.parse(detailHtml, BASE_URL);
		String description = extractDescription(document);
		String employmentType = extractEmploymentType(document);
		if (employmentType == null) {
			employmentType = summary.employmentType();
		}
		String workMode = extractWorkMode(document, summary.location());
		BigDecimal[] salary = SalaryParser.parseRange(extractSalaryText(document));
		String title = firstText(document, "h2.jobsearch-JobInfoHeader-title, h1.jobsearch-JobInfoHeader-title");
		String company = firstText(document, "[data-testid='inlineHeader-companyName'], [data-testid='company-name']");

		return Optional.of(new ScrapedJob(
				summary.platform(),
				summary.externalId(),
				title != null ? title : summary.title(),
				company != null ? company : summary.companyName(),
				description != null ? description : summary.description(),
				summary.location(),
				salary[0] != null ? salary[0] : summary.salaryMin(),
				salary[1] != null ? salary[1] : summary.salaryMax(),
				summary.url(),
				summary.postedAt(),
				employmentType,
				workMode != null ? workMode : summary.workMode(),
				summary.category(),
				summary.subcategory(),
				summary.benefits(),
				summary.requirements()));
	}

	private boolean isHiddenCard(Element card) {
		Element outline = card.closest("div.cardOutline");
		if (outline != null && "true".equalsIgnoreCase(outline.attr("aria-hidden"))) {
			return true;
		}
		return "true".equalsIgnoreCase(card.attr("aria-hidden"));
	}

	private String extractDescription(Document document) {
		Element description = document.selectFirst(
				"#jobDescriptionText, div.jobsearch-JobComponent-description, [id*='jobDescriptionText']");
		if (description == null) {
			return null;
		}
		String text = description.text().trim();
		return text.isEmpty() ? null : text;
	}

	private String extractSalaryText(Document document) {
		Element salary = document.selectFirst(
				"#salaryInfoAndJobType, [data-testid='jobsearch-OtherJobDetailsContainer'], "
						+ ".jobsearch-JobMetadataHeader-item");
		if (salary != null) {
			return salary.text();
		}
		for (Element snippet : document.select("span.css-zydy3i, .salary-snippet")) {
			String text = snippet.text().trim();
			if (text.contains("$") || text.toLowerCase().contains("por mes")) {
				return text;
			}
		}
		return null;
	}

	private String extractEmploymentType(Document document) {
		for (Element item : document.select("li[data-testid*='attribute_snippet'], span.css-zydy3i")) {
			String text = item.text().trim();
			if (isEmploymentType(text)) {
				return text;
			}
		}
		return null;
	}

	private String extractWorkMode(Document document, String fallbackLocation) {
		String location = firstText(document, "[data-testid='text-location'], [data-testid='job-location']");
		if (location == null) {
			location = fallbackLocation;
		}
		return mapLocationToWorkMode(location);
	}

	private boolean isEmploymentType(String text) {
		String lower = text.toLowerCase();
		return lower.contains("tiempo completo")
				|| lower.contains("medio tiempo")
				|| lower.contains("temporal")
				|| lower.contains("contrato")
				|| lower.contains("full-time")
				|| lower.contains("part-time");
	}

	private String mapLocationToWorkMode(String location) {
		if (location == null || location.isBlank()) {
			return null;
		}
		String lower = location.toLowerCase();
		if (lower.contains("desde casa") || lower.contains("remoto") || lower.contains("remote")) {
			return "remote";
		}
		if (lower.contains("híbrido") || lower.contains("hibrido") || lower.contains("hybrid")) {
			return "hybrid";
		}
		return null;
	}

	private ScrapedJob parseCard(Element card) {
		Element link = card.selectFirst("h3.jobTitle a.jcs-JobTitle, a.jcs-JobTitle[data-jk], a[data-jk][href*='jk=']");
		if (link == null) {
			return null;
		}

		String jobKey = link.attr("data-jk");
		if (jobKey.isBlank()) {
			jobKey = extractJobKey(link.absUrl("href"));
		}
		if (jobKey == null || jobKey.isBlank()) {
			return null;
		}

		String title = firstText(card, "h3.jobTitle span[title], span[id^='jobTitle-']");
		if (title == null) {
			title = link.attr("aria-label");
			if (title != null && title.toLowerCase().startsWith("detalles completos de ")) {
				title = title.substring("detalles completos de ".length()).trim();
			}
		}
		if (title == null || title.isBlank()) {
			title = link.text();
		}
		if (title == null || title.isBlank()) {
			return null;
		}

		String company = firstText(card, "[data-testid='company-name']");
		String location = firstText(card, "[data-testid='text-location']");
		String salaryText = extractCardSalaryText(card);
		BigDecimal[] salary = SalaryParser.parseRange(salaryText);
		String employmentType = extractCardEmploymentType(card);
		String workMode = mapLocationToWorkMode(location);

		return new ScrapedJob(
				JobPlatform.INDEED,
				jobKey,
				title,
				company,
				null,
				location,
				salary[0],
				salary[1],
				viewJobUrl(jobKey),
				null,
				employmentType,
				workMode,
				null,
				null,
				null,
				null);
	}

	private String extractCardSalaryText(Element card) {
		Element salaryContainer = card.selectFirst("li.salary-snippet-container, .salary-snippet-container");
		if (salaryContainer != null) {
			for (Element span : salaryContainer.select("span.css-zydy3i, .salary-snippet")) {
				String text = span.text().trim();
				if (text.contains("$") || text.toLowerCase().contains("por mes")) {
					return text;
				}
			}
		}
		return firstText(card, ".salary-snippet, [data-testid*='salary-snippet']");
	}

	private String extractCardEmploymentType(Element card) {
		for (Element item : card.select("li[data-testid*='attribute_snippet'] span.css-zydy3i")) {
			String text = item.text().trim();
			if (isEmploymentType(text)) {
				return text.split("\\s+\\+")[0].trim();
			}
		}
		return null;
	}

	private String viewJobUrl(String jobKey) {
		return BASE_URL + "/viewjob?jk=" + jobKey;
	}

	private String extractJobKey(String url) {
		if (url == null) {
			return null;
		}
		Matcher matcher = JOB_KEY_PATTERN.matcher(url);
		return matcher.find() ? matcher.group(1) : null;
	}

	private String firstText(Element root, String selector) {
		for (String part : selector.split(",")) {
			Element element = root.selectFirst(part.trim());
			if (element != null) {
				String text = element.text().trim();
				if (!text.isEmpty()) {
					return text;
				}
			}
		}
		return null;
	}

	private String firstText(Document document, String selector) {
		return firstText(document.body(), selector);
	}
}
