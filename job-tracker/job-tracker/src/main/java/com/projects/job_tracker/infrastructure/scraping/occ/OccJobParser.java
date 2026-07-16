package com.projects.job_tracker.infrastructure.scraping.occ;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapedJob;
import com.projects.job_tracker.infrastructure.scraping.SalaryParser;

public class OccJobParser {

	private static final Logger log = LoggerFactory.getLogger(OccJobParser.class);

	private static final String BASE_URL = "https://www.occ.com.mx";
	private static final Pattern OFFER_ID_PATTERN = Pattern.compile("/empleo[s]?/oferta/(\\d+)");

	public List<ScrapedJob> parse(String html, int maxResults) {
		Document document = Jsoup.parse(html, BASE_URL);
		List<ScrapedJob> jobs = new ArrayList<>();
		Set<String> seenUrls = new LinkedHashSet<>();

		Elements cards = document.select(".card-job-offer");
		String cardStrategy = ".card-job-offer";
		if (cards.isEmpty()) {
			cards = document.select("div[data-offer-id]");
			cardStrategy = "div[data-offer-id]";
		}
		if (cards.isEmpty()) {
			cards = wrapAnchors(document.select("a[href*='/empleo/oferta/'], a[href*='/empleos/oferta/']"));
			cardStrategy = "a[href*='/empleo/oferta/']";
		}

		log.info(
				"OCC parse start: pageTitle='{}', htmlLength={}, cards={}, strategy={}",
				document.title(),
				html.length(),
				cards.size(),
				cardStrategy);

		for (Element card : cards) {
			ScrapedJob job = parseListCard(card);
			if (job != null && seenUrls.add(job.url())) {
				jobs.add(job);
				if (jobs.size() >= maxResults) {
					break;
				}
			}
		}

		if (jobs.isEmpty()) {
			Optional<ScrapedJob> detailJob = parseDetailPanel(html);
			detailJob.ifPresent(jobs::add);
		}

		log.info("OCC parse done: parsed={}, maxResults={}", jobs.size(), maxResults);
		return jobs;
	}

	public Optional<ScrapedJob> parseDetailPanel(String detailHtml) {
		Document document = Jsoup.parseBodyFragment(detailHtml, BASE_URL);
		return parseDetailDocument(document);
	}

	private Optional<ScrapedJob> parseDetailDocument(Document document) {
		Element titleElement = document.selectFirst("[data-offers-grid-detail-title]");
		if (titleElement == null) {
			return Optional.empty();
		}

		String title = titleElement.text().trim();
		if (title.isBlank()) {
			return Optional.empty();
		}

		String url = extractOfferUrl(document);
		if (url == null || url.isBlank()) {
			return Optional.empty();
		}

		String company = extractCompany(document);
		String location = extractLocation(document);
		String salaryText = extractSalaryText(document);
		BigDecimal[] salary = SalaryParser.parseRange(salaryText);
		String externalId = extractOfferId(url);

		return Optional.of(buildFromDetail(
				title,
				url,
				externalId,
				company,
				location,
				salary[0],
				salary[1],
				document));
	}

	private ScrapedJob buildFromDetail(
			String title,
			String url,
			String externalId,
			String company,
			String location,
			BigDecimal salaryMin,
			BigDecimal salaryMax,
			Document document) {
		return new ScrapedJob(
				JobPlatform.OCC,
				externalId,
				title,
				company,
				extractDescription(document),
				location,
				salaryMin,
				salaryMax,
				url,
				null,
				extractLabeledValue(document, "Contratación"),
				extractWorkMode(document),
				extractCategory(document),
				extractSubcategory(document),
				extractBenefits(document),
				extractRequirements(document));
	}

	private ScrapedJob parseListCard(Element card) {
		Element link = card.selectFirst("a[href*='/empleo/oferta/'], a[href*='/empleos/oferta/']");
		String url = link != null ? link.absUrl("href") : null;
		if (url == null || url.isBlank()) {
			url = firstAttr(card, "[data-url]", "data-url");
		}
		if (url == null || url.isBlank()) {
			return null;
		}

		String title = firstText(card, "[data-offers-grid-detail-title], h2, h3, [class*='job-title'], [class*='title']");
		if (title == null && link != null) {
			title = link.text();
		}
		if (title == null || title.isBlank()) {
			return null;
		}

		String company = firstText(card, "[class*='company'], .company-name, .line-clamp-1");
		String location = firstText(card, "[class*='location'], .location, label.font-light");
		String salaryText = firstText(card, "[class*='salary'], .salary, .icon.i_money");
		BigDecimal[] salary = SalaryParser.parseRange(salaryText);
		String externalId = card.hasAttr("data-offer-id") ? card.attr("data-offer-id") : extractOfferId(url);

		return new ScrapedJob(
				JobPlatform.OCC,
				externalId,
				title,
				company,
				null,
				location,
				salary[0],
				salary[1],
				normalizeUrl(url),
				null,
				null,
				null,
				null,
				null,
				null,
				null);
	}

	private String extractDescription(Document document) {
		Element richText = document.selectFirst(".rich-text");
		if (richText == null) {
			return null;
		}
		String text = richText.text().trim();
		return text.isEmpty() ? null : text;
	}

	private String extractCategory(Document document) {
		Element row = findDetailRow(document, "Categoría");
		if (row == null) {
			return null;
		}
		Element link = row.selectFirst("a");
		return link != null ? link.text().trim() : null;
	}

	private String extractSubcategory(Document document) {
		Element row = findDetailRow(document, "Subcategoría");
		if (row == null) {
			return null;
		}
		Element link = row.selectFirst("a");
		return link != null ? link.text().trim() : null;
	}

	private String extractWorkMode(Document document) {
		Element row = findDetailRow(document, "Espacio de trabajo");
		if (row == null) {
			return null;
		}
		Element link = row.selectFirst("a");
		if (link != null) {
			return link.text().trim();
		}
		return extractLabeledValue(document, "Espacio de trabajo");
	}

	private String extractBenefits(Document document) {
		Element heading = findHeading(document, "Beneficios");
		if (heading == null) {
			return null;
		}
		Element list = heading.nextElementSibling();
		if (list == null) {
			list = heading.parent() != null ? heading.parent().selectFirst("ul") : null;
		}
		if (list == null) {
			return null;
		}
		String text = list.text().trim();
		return text.isEmpty() ? null : text;
	}

	private String extractRequirements(Document document) {
		Element requirements = document.selectFirst("p:containsOwn(Requerimientos)");
		if (requirements != null) {
			Element section = requirements.parent();
			if (section != null) {
				String text = section.text().trim();
				return text.isEmpty() ? null : text;
			}
		}
		return null;
	}

	private String extractLabeledValue(Document document, String label) {
		Element row = findDetailRow(document, label);
		if (row == null) {
			return null;
		}
		String text = row.text().replace(label + ":", "").replace(label, "").trim();
		return text.isEmpty() ? null : text;
	}

	private Element findDetailRow(Document document, String label) {
		for (Element div : document.select("div")) {
			Element labelElement = div.selectFirst("p.mr-1, p");
			if (labelElement != null && labelElement.text().trim().startsWith(label)) {
				return div;
			}
		}
		return null;
	}

	private Element findHeading(Document document, String text) {
		for (Element heading : document.select("p.text-lg, p.mb-4")) {
			if (heading.text().trim().equalsIgnoreCase(text)) {
				return heading;
			}
		}
		return null;
	}

	private String extractOfferUrl(Document document) {
		Element share = document.selectFirst("[job-share-container][data-url], [data-url*='empleo/oferta']");
		if (share != null) {
			return normalizeUrl(share.attr("data-url"));
		}
		Element link = document.selectFirst("a[href*='/empleo/oferta/'], a[href*='/empleos/oferta/']");
		return link != null ? normalizeUrl(link.absUrl("href")) : null;
	}

	private String extractCompany(Document document) {
		Element block = document.selectFirst(".line-clamp-1");
		if (block == null) {
			return null;
		}
		String text = block.text().trim();
		int enIndex = text.toLowerCase().indexOf(" en ");
		if (enIndex > 0) {
			return text.substring(0, enIndex).trim();
		}
		return text.isEmpty() ? null : text;
	}

	private String extractLocation(Document document) {
		Element label = document.selectFirst(".line-clamp-1 label.font-light");
		if (label != null) {
			String location = label.text().trim();
			return location.isEmpty() ? null : location;
		}
		Element block = document.selectFirst(".line-clamp-1");
		if (block == null) {
			return null;
		}
		String text = block.text().trim();
		int enIndex = text.toLowerCase().lastIndexOf(" en ");
		if (enIndex >= 0 && enIndex + 4 < text.length()) {
			return text.substring(enIndex + 4).trim();
		}
		return null;
	}

	private String extractSalaryText(Document document) {
		Element moneyIcon = document.selectFirst(".icon.i_money");
		if (moneyIcon != null) {
			Element parent = moneyIcon.parent();
			if (parent != null) {
				return parent.text().trim();
			}
		}
		return firstText(document, "[class*='salary'], .salary");
	}

	private String extractOfferId(String url) {
		Matcher matcher = OFFER_ID_PATTERN.matcher(url);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return url;
	}

	private String normalizeUrl(String url) {
		if (url == null || url.isBlank()) {
			return url;
		}
		if (url.startsWith("http://")) {
			return "https://" + url.substring("http://".length());
		}
		return url;
	}

	private Elements wrapAnchors(Elements anchors) {
		Elements cards = new Elements();
		for (Element anchor : anchors) {
			Element card = anchor.closest("div, li, article");
			cards.add(card != null ? card : anchor);
		}
		return cards;
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
		Element element = root.selectFirst(selector);
		if (element == null) {
			return null;
		}
		String text = element.text().trim();
		return text.isEmpty() ? null : text;
	}
}
