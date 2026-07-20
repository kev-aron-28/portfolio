package com.projects.job_tracker.presentation.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projects.job_tracker.application.analytics.GetDashboardMetricsUseCase;
import com.projects.job_tracker.application.analytics.ListJobListingsUseCase;
import com.projects.job_tracker.application.profile.ListSearchProfilesUseCase;
import com.projects.job_tracker.application.scraping.GetScrapingSettingsUseCase;
import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.application.segment.CreateMarketSegmentUseCase;
import com.projects.job_tracker.application.segment.DeleteMarketSegmentUseCase;
import com.projects.job_tracker.application.segment.GetMarketSegmentUseCase;
import com.projects.job_tracker.application.segment.ListMarketSegmentsUseCase;
import com.projects.job_tracker.application.segment.UpdateMarketSegmentUseCase;
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;
import com.projects.job_tracker.infrastructure.scraping.ScrapeFilterSetParser;

@Controller
@RequestMapping("/segments")
public class MarketSegmentWebController {

	private final ListMarketSegmentsUseCase listMarketSegmentsUseCase;
	private final GetMarketSegmentUseCase getMarketSegmentUseCase;
	private final CreateMarketSegmentUseCase createMarketSegmentUseCase;
	private final UpdateMarketSegmentUseCase updateMarketSegmentUseCase;
	private final DeleteMarketSegmentUseCase deleteMarketSegmentUseCase;
	private final GetDashboardMetricsUseCase getDashboardMetricsUseCase;
	private final ListJobListingsUseCase listJobListingsUseCase;
	private final ListSearchProfilesUseCase listSearchProfilesUseCase;
	private final ScrapeJobsUseCase scrapeJobsUseCase;
	private final GetScrapingSettingsUseCase getScrapingSettingsUseCase;
	private final MarketSegmentRepository marketSegmentRepository;

	public MarketSegmentWebController(
			ListMarketSegmentsUseCase listMarketSegmentsUseCase,
			GetMarketSegmentUseCase getMarketSegmentUseCase,
			CreateMarketSegmentUseCase createMarketSegmentUseCase,
			UpdateMarketSegmentUseCase updateMarketSegmentUseCase,
			DeleteMarketSegmentUseCase deleteMarketSegmentUseCase,
			GetDashboardMetricsUseCase getDashboardMetricsUseCase,
			ListJobListingsUseCase listJobListingsUseCase,
			ListSearchProfilesUseCase listSearchProfilesUseCase,
			ScrapeJobsUseCase scrapeJobsUseCase,
			GetScrapingSettingsUseCase getScrapingSettingsUseCase,
			MarketSegmentRepository marketSegmentRepository) {
		this.listMarketSegmentsUseCase = listMarketSegmentsUseCase;
		this.getMarketSegmentUseCase = getMarketSegmentUseCase;
		this.createMarketSegmentUseCase = createMarketSegmentUseCase;
		this.updateMarketSegmentUseCase = updateMarketSegmentUseCase;
		this.deleteMarketSegmentUseCase = deleteMarketSegmentUseCase;
		this.getDashboardMetricsUseCase = getDashboardMetricsUseCase;
		this.listJobListingsUseCase = listJobListingsUseCase;
		this.listSearchProfilesUseCase = listSearchProfilesUseCase;
		this.scrapeJobsUseCase = scrapeJobsUseCase;
		this.getScrapingSettingsUseCase = getScrapingSettingsUseCase;
		this.marketSegmentRepository = marketSegmentRepository;
	}

	@GetMapping(produces = MediaType.TEXT_HTML_VALUE)
	public String list(Model model) {
		List<SegmentSummary> summaries = listMarketSegmentsUseCase.execute().stream()
				.map(segment -> new SegmentSummary(segment, marketSegmentRepository.countJobs(segment.id())))
				.toList();
		model.addAttribute("segments", summaries);
		model.addAttribute("pageTitle", "Segmentos");
		model.addAttribute("activeNav", "segments");
		model.addAttribute("pageDescription", summaries.size() + " segmento" + (summaries.size() != 1 ? "s" : ""));
		return "segments/list";
	}

	@GetMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE)
	public String newForm(Model model) {
		model.addAttribute("pageTitle", "Nuevo segmento");
		model.addAttribute("activeNav", "segments");
		model.addAttribute("pageDescription", "Define un mercado y sus criterios de búsqueda");
		return "segments/form";
	}

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String create(
			@RequestParam String name,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) String keywords,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) BigDecimal salaryMin,
			@RequestParam(required = false) BigDecimal salaryMax,
			@RequestParam(required = false) String employmentType,
			@RequestParam(required = false) String workMode,
			@RequestParam(required = false) Integer postedWithinDays,
			RedirectAttributes redirectAttributes) {
		try {
			ScrapeFilterSet filters = new ScrapeFilterSet(
					location, salaryMin, salaryMax, employmentType, workMode, postedWithinDays);
			MarketSegment created = createMarketSegmentUseCase.execute(
					new CreateMarketSegmentUseCase.CreateMarketSegmentCommand(
							name,
							description,
							keywords,
							ScrapeFilterSetParser.toJson(filters)));
			redirectAttributes.addFlashAttribute("successMessage", "Segmento creado.");
			return "redirect:/segments/" + created.id();
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
			return "redirect:/segments/new";
		}
	}

	@GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
	public String detail(@PathVariable Long id, Model model) {
		MarketSegment segment = getMarketSegmentUseCase.execute(id);
		DashboardMetrics metrics = getDashboardMetricsUseCase.execute(id);
		List<JobListing> recentJobs = listJobListingsUseCase
				.execute(new ListJobListingsUseCase.JobListingQuery(
						null, null, null, null, null, null, null, null, null, null, false, id,
						com.projects.job_tracker.domain.model.JobSortField.CREATED_AT,
						com.projects.job_tracker.domain.model.SortDirection.DESC))
				.stream()
				.limit(5)
				.toList();
		List<SearchProfile> profiles = listSearchProfilesUseCase.execute();
		ScrapeFilterSet filters = ScrapeFilterSetParser.parse(segment.filters());

		model.addAttribute("segment", segment);
		model.addAttribute("metrics", metrics);
		model.addAttribute("recentJobs", recentJobs);
		model.addAttribute("profiles", profiles);
		model.addAttribute("filters", filters);
		model.addAttribute("settings", getScrapingSettingsUseCase.execute());
		model.addAttribute("allPlatforms", Arrays.asList(JobPlatform.values()));
		model.addAttribute("jobCount", marketSegmentRepository.countJobs(id));
		model.addAttribute("pageTitle", segment.name());
		model.addAttribute("activeNav", "segments");
		model.addAttribute("breadcrumbSection", "segments");
		model.addAttribute(
				"pageDescription",
				segment.keywords() != null ? segment.keywords() : "Sin keywords configurados");
		return "segments/detail";
	}

	@PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String update(
			@PathVariable Long id,
			@RequestParam String name,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) String keywords,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) BigDecimal salaryMin,
			@RequestParam(required = false) BigDecimal salaryMax,
			@RequestParam(required = false) String employmentType,
			@RequestParam(required = false) String workMode,
			@RequestParam(required = false) Integer postedWithinDays,
			RedirectAttributes redirectAttributes) {
		try {
			ScrapeFilterSet filters = new ScrapeFilterSet(
					location, salaryMin, salaryMax, employmentType, workMode, postedWithinDays);
			updateMarketSegmentUseCase.execute(
					id,
					new UpdateMarketSegmentUseCase.UpdateMarketSegmentCommand(
							name,
							description,
							keywords,
							ScrapeFilterSetParser.toJson(filters)));
			redirectAttributes.addFlashAttribute("successMessage", "Segmento actualizado.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/segments/" + id;
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			deleteMarketSegmentUseCase.execute(id);
			redirectAttributes.addFlashAttribute("successMessage", "Segmento eliminado.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
			return "redirect:/segments/" + id;
		}
		return "redirect:/segments";
	}

	@PostMapping(value = "/{id}/scrape", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String scrape(
			@PathVariable Long id,
			@RequestParam(required = false) Long profileId,
			@RequestParam(required = false) String keywords,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) BigDecimal salaryMin,
			@RequestParam(required = false) BigDecimal salaryMax,
			@RequestParam(required = false) String employmentType,
			@RequestParam(required = false) String workMode,
			@RequestParam(required = false) Integer postedWithinDays,
			@RequestParam(required = false) List<String> platforms,
			@RequestParam(required = false) Integer maxResults,
			RedirectAttributes redirectAttributes) {
		try {
			List<JobPlatform> resolvedPlatforms = resolvePlatforms(platforms);
			int resolvedMax = maxResults != null
					? maxResults
					: getScrapingSettingsUseCase.execute().defaultMaxResults();
			ScrapeJobsUseCase.ScrapeResult result = scrapeJobsUseCase.execute(new ScrapeJobsUseCase.ScrapeJobsCommand(
					profileId,
					keywords,
					location,
					salaryMin,
					salaryMax,
					employmentType,
					workMode,
					postedWithinDays,
					resolvedPlatforms,
					resolvedMax,
					id));
			redirectAttributes.addFlashAttribute("scrapeResult", result);
			redirectAttributes.addFlashAttribute(
					"successMessage",
					"Búsqueda del segmento: " + result.imported() + " nuevas, "
							+ result.duplicates() + " duplicadas (asociadas), "
							+ result.scraped() + " encontradas.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/segments/" + id;
	}

	private List<JobPlatform> resolvePlatforms(List<String> platforms) {
		if (platforms == null || platforms.isEmpty()) {
			return List.copyOf(getScrapingSettingsUseCase.execute().defaultPlatforms());
		}
		return platforms.stream().map(JobPlatform::fromSource).toList();
	}

	public record SegmentSummary(MarketSegment segment, long jobCount) {
	}
}
