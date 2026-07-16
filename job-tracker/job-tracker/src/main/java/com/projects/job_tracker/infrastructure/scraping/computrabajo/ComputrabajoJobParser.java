package com.projects.job_tracker.infrastructure.scraping.computrabajo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.infrastructure.scraping.SalaryParser;

public class ComputrabajoJobParser {

	private static final String BASE_URL = "https://mx.computrabajo.com";

	public List<ScrapedJob> parse(String html, int maxResults) {
		Document document = Jsoup.parse(html, BASE_URL);
		List<ScrapedJob> jobs = new ArrayList<>();
		Set<String> seenIds = new LinkedHashSet<>();

		Elements cards = document.select("article.box_offer[data-offers-grid-offer-item-container]");
		if (cards.isEmpty()) {
			cards = document.select("article.box_offer[data-id]");
		}

		for (Element card : cards) {
			ScrapedJob job = parseListCard(card);
			if (job != null && seenIds.add(job.externalId())) {
				jobs.add(job);
				if (jobs.size() >= maxResults) {
					break;
				}
			}
		}
		return jobs;
	}

	public Optional<ScrapedJob> parseDetailPanel(String detailHtml) {
		Document document = Jsoup.parseBodyFragment(detailHtml, BASE_URL);
		String externalId = firstAttr(document, "[data-oi]", "data-oi");
		if (externalId == null) {
			return Optional.empty();
		}
		return buildDetailJob(document, externalId, BASE_URL + "/ofertas-de-trabajo/?oi=" + externalId);
	}

	public Optional<ScrapedJob> enrichWithDetail(ScrapedJob summary, String detailHtml) {
		Document document = Jsoup.parseBodyFragment(detailHtml, BASE_URL);
		return buildDetailJob(document, summary.externalId(), summary.url())
				.map(detail -> new ScrapedJob(
						summary.platform(),
						summary.externalId(),
						detail.title() != null ? detail.title() : summary.title(),
						detail.companyName() != null ? detail.companyName() : summary.companyName(),
						detail.description() != null ? detail.description() : summary.description(),
						detail.location() != null ? detail.location() : summary.location(),
						detail.salaryMin() != null ? detail.salaryMin() : summary.salaryMin(),
						detail.salaryMax() != null ? detail.salaryMax() : summary.salaryMax(),
						summary.url(),
						summary.postedAt(),
						detail.employmentType() != null ? detail.employmentType() : summary.employmentType(),
						detail.workMode() != null ? detail.workMode() : summary.workMode(),
						summary.category(),
						summary.subcategory(),
						detail.benefits(),
						detail.requirements()));
	}

	private Optional<ScrapedJob> buildDetailJob(Document document, String externalId, String url) {
		String title = firstText(document, "[data-offers-grid-detail-title], .title_offer");
		if (title == null || title.isBlank()) {
			return Optional.empty();
		}

		String company = firstText(document, ".header_detail p.fwB.fs16, .header_detail .fwB.fs16");
		String location = firstText(document, ".header_detail p.fs16.mb5");
		String salaryText = extractIconRowText(document, "i_money");
		BigDecimal[] salary = SalaryParser.parseRange(salaryText);
		String employmentType = extractIconRowText(document, "i_clock");
		String workMode = mapWorkModeText(extractWorkModeFromDetail(document, location));
		String description = extractDescription(document);
		String requirements = extractRequirements(document);

		return Optional.of(new ScrapedJob(
				JobPlatform.COMPUTRABAJO,
				externalId,
				title,
				company,
				description,
				location,
				salary[0],
				salary[1],
				url,
				null,
				employmentType,
				workMode,
				null,
				null,
				null,
				requirements));
	}

	private ScrapedJob parseListCard(Element card) {
		String externalId = card.attr("data-id");
		if (externalId.isBlank()) {
			externalId = card.id();
		}
		if (externalId.isBlank()) {
			return null;
		}

		Element link = card.selectFirst("h2.jobTitle a.js-o-link, h2 a.js-o-link, a.js-o-link");
		if (link == null) {
			return null;
		}

		String title = link.text().trim();
		if (title.isBlank()) {
			return null;
		}

		String company = firstText(card, "a[offer-grid-article-company-url]");
		String location = firstText(card, "p.fs16.fc_base.mt5 span, p.fs16.fc_base.mt5");
		String salaryText = extractIconRowText(card, "i_salary");
		BigDecimal[] salary = SalaryParser.parseRange(salaryText);
		String workMode = mapWorkModeText(extractWorkModeFromCard(card));

		return new ScrapedJob(
				JobPlatform.COMPUTRABAJO,
				externalId,
				title,
				company,
				null,
				location,
				salary[0],
				salary[1],
				normalizeUrl(link.absUrl("href")),
				null,
				null,
				workMode,
				null,
				null,
				null,
				null);
	}

	private String extractDescription(Document document) {
		Element description = document.selectFirst(".description_offer .t_word_wrap, .description_offer");
		if (description == null) {
			return null;
		}
		String text = description.text().trim();
		return text.isEmpty() ? null : text;
	}

	private String extractRequirements(Document document) {
		Element heading = null;
		for (Element element : document.select("p.fwB.fs18")) {
			if ("Requerimientos".equalsIgnoreCase(element.text().trim())) {
				heading = element;
				break;
			}
		}
		if (heading == null) {
			return null;
		}
		Element list = heading.nextElementSibling();
		if (list == null) {
			return null;
		}
		String text = list.text().trim();
		return text.isEmpty() ? null : text;
	}

	private String extractWorkModeFromCard(Element card) {
		String fromIcon = extractIconRowText(card, "i_home_office");
		if (fromIcon != null) {
			return fromIcon;
		}
		fromIcon = extractIconRowText(card, "i_home");
		if (fromIcon != null) {
			return fromIcon;
		}
		return null;
	}

	private String extractWorkModeFromDetail(Document document, String fallbackLocation) {
		String remote = extractIconRowText(document, "i_home");
		if (remote != null) {
			return remote;
		}
		String hybrid = extractIconRowText(document, "i_home_office");
		if (hybrid != null) {
			return hybrid;
		}
		String onsite = extractIconRowText(document, "i_company");
		if (onsite != null) {
			return onsite;
		}
		return fallbackLocation;
	}

	private String extractIconRowText(Element root, String iconClass) {
		Element icon = root.selectFirst(".icon." + iconClass);
		if (icon == null) {
			return null;
		}
		Element row = icon.parent();
		if (row == null) {
			return null;
		}
		String text = row.text().trim();
		return text.isEmpty() ? null : text;
	}

	private String mapWorkModeText(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String lower = value.toLowerCase();
		if (lower.contains("remoto") && !lower.contains("presencial")) {
			return "remote";
		}
		if (lower.contains("presencial y remoto") || lower.contains("híbrido") || lower.contains("hibrido")) {
			return "hybrid";
		}
		if (lower.contains("presencial")) {
			return "onsite";
		}
		return null;
	}

	private String normalizeUrl(String url) {
		if (url == null || url.isBlank()) {
			return url;
		}
		int hashIndex = url.indexOf('#');
		if (hashIndex >= 0) {
			url = url.substring(0, hashIndex);
		}
		return url;
	}

	private String firstAttr(Element root, String selector, String attribute) {
		Element element = root.selectFirst(selector);
		if (element == null || !element.hasAttr(attribute)) {
			return null;
		}
		String value = element.attr(attribute).trim();
		return value.isEmpty() ? null : value;
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
