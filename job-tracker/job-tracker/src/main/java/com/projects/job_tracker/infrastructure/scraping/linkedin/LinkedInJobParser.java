package com.projects.job_tracker.infrastructure.scraping.linkedin;

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

public class LinkedInJobParser {

	private static final String BASE_URL = "https://www.linkedin.com";
	private static final Pattern JOB_ID_PATTERN = Pattern.compile("/jobs/view/(\\d+)");

	public List<ScrapedJob> parse(String html, int maxResults) {
		Document document = Jsoup.parse(html, BASE_URL);
		List<ScrapedJob> jobs = new ArrayList<>();
		Set<String> seenUrls = new LinkedHashSet<>();

		Elements cards = document.select("li[data-occludable-job-id], div.base-card, [class*='base-card']");
		if (cards.isEmpty()) {
			cards = wrapAnchors(document.select("a[href*='/jobs/view/']"));
		}

		for (Element card : cards) {
			ScrapedJob job = parseCard(card);
			if (job != null && seenUrls.add(job.url())) {
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
		String employmentType = extractCriteriaText(document, "Employment type");
		String workMode = extractCriteriaText(document, "Workplace type");
		BigDecimal[] salary = SalaryParser.parseRange(extractCriteriaText(document, "Base pay"));

		return Optional.of(new ScrapedJob(
				summary.platform(),
				summary.externalId(),
				summary.title(),
				summary.companyName(),
				description != null ? description : summary.description(),
				summary.location(),
				salary[0] != null ? salary[0] : summary.salaryMin(),
				salary[1] != null ? salary[1] : summary.salaryMax(),
				summary.url(),
				summary.postedAt(),
				employmentType != null ? employmentType : summary.employmentType(),
				workMode != null ? workMode : summary.workMode(),
				summary.category(),
				summary.subcategory(),
				summary.benefits(),
				summary.requirements()));
	}

	private String extractDescription(Document document) {
		Element description = document.selectFirst(
				".show-more-less-html__markup, .description__text, [class*='description__text']");
		if (description == null) {
			return null;
		}
		String text = description.text().trim();
		return text.isEmpty() ? null : text;
	}

	private String extractCriteriaText(Document document, String label) {
		for (Element item : document.select("li, span, div")) {
			String text = item.text().trim();
			if (text.startsWith(label)) {
				String value = text.substring(label.length()).replace(":", "").trim();
				return value.isEmpty() ? null : value;
			}
		}
		return null;
	}

	private Elements wrapAnchors(Elements anchors) {
		Elements cards = new Elements();
		for (Element anchor : anchors) {
			Element card = anchor.closest("li, div.base-card, article");
			cards.add(card != null ? card : anchor);
		}
		return cards;
	}

	private ScrapedJob parseCard(Element card) {
		Element link = card.selectFirst("a[href*='/jobs/view/']");
		if (link == null) {
			return null;
		}

		String href = link.absUrl("href");
		if (href.isBlank()) {
			return null;
		}
		int queryIndex = href.indexOf('?');
		if (queryIndex > 0) {
			href = href.substring(0, queryIndex);
		}

		String title = firstText(card, "[class*='base-search-card__title'], h3, [class*='job-card-title']");
		if (title == null) {
			title = link.text();
		}
		if (title == null || title.isBlank()) {
			return null;
		}

		String company = firstText(card, "[class*='subtitle'], [class*='company-name'], h4");
		String location = firstText(card, "[class*='location'], [class*='metadata']");
		String externalId = card.hasAttr("data-occludable-job-id")
				? card.attr("data-occludable-job-id")
				: extractJobId(href);

		return new ScrapedJob(
				JobPlatform.LINKEDIN,
				externalId,
				title,
				company,
				null,
				location,
				null,
				null,
				href,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}

	private String extractJobId(String url) {
		Matcher matcher = JOB_ID_PATTERN.matcher(url);
		return matcher.find() ? matcher.group(1) : url;
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
}
