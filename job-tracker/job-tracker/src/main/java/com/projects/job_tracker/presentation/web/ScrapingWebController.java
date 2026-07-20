package com.projects.job_tracker.presentation.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projects.job_tracker.application.automation.CreateScrapingScheduleUseCase;
import com.projects.job_tracker.application.automation.ListScrapingSchedulesUseCase;
import com.projects.job_tracker.application.automation.RunScheduledScrapingUseCase;
import com.projects.job_tracker.application.automation.UpdateScrapingScheduleUseCase;
import com.projects.job_tracker.application.profile.CreateSearchProfileUseCase;
import com.projects.job_tracker.application.profile.ListSearchProfilesUseCase;
import com.projects.job_tracker.application.scraping.GetScrapingSettingsUseCase;
import com.projects.job_tracker.application.scraping.ScrapeJobsUseCase;
import com.projects.job_tracker.application.scraping.UpdateScrapingSettingsUseCase;
import com.projects.job_tracker.application.segment.ListMarketSegmentsUseCase;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapeFilterSet;
import com.projects.job_tracker.domain.model.ScrapingSchedule;
import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.domain.model.SearchProfile;
import com.projects.job_tracker.infrastructure.scraping.ScrapeFilterSetParser;

@Controller
@RequestMapping("/scraping")
public class ScrapingWebController {

	private final ScrapeJobsUseCase scrapeJobsUseCase;
	private final GetScrapingSettingsUseCase getScrapingSettingsUseCase;
	private final UpdateScrapingSettingsUseCase updateScrapingSettingsUseCase;
	private final ListSearchProfilesUseCase listSearchProfilesUseCase;
	private final CreateSearchProfileUseCase createSearchProfileUseCase;
	private final ListScrapingSchedulesUseCase listScrapingSchedulesUseCase;
	private final CreateScrapingScheduleUseCase createScrapingScheduleUseCase;
	private final UpdateScrapingScheduleUseCase updateScrapingScheduleUseCase;
	private final RunScheduledScrapingUseCase runScheduledScrapingUseCase;
	private final ListMarketSegmentsUseCase listMarketSegmentsUseCase;

	public ScrapingWebController(
			ScrapeJobsUseCase scrapeJobsUseCase,
			GetScrapingSettingsUseCase getScrapingSettingsUseCase,
			UpdateScrapingSettingsUseCase updateScrapingSettingsUseCase,
			ListSearchProfilesUseCase listSearchProfilesUseCase,
			CreateSearchProfileUseCase createSearchProfileUseCase,
			ListScrapingSchedulesUseCase listScrapingSchedulesUseCase,
			CreateScrapingScheduleUseCase createScrapingScheduleUseCase,
			UpdateScrapingScheduleUseCase updateScrapingScheduleUseCase,
			RunScheduledScrapingUseCase runScheduledScrapingUseCase,
			ListMarketSegmentsUseCase listMarketSegmentsUseCase) {
		this.scrapeJobsUseCase = scrapeJobsUseCase;
		this.getScrapingSettingsUseCase = getScrapingSettingsUseCase;
		this.updateScrapingSettingsUseCase = updateScrapingSettingsUseCase;
		this.listSearchProfilesUseCase = listSearchProfilesUseCase;
		this.createSearchProfileUseCase = createSearchProfileUseCase;
		this.listScrapingSchedulesUseCase = listScrapingSchedulesUseCase;
		this.createScrapingScheduleUseCase = createScrapingScheduleUseCase;
		this.updateScrapingScheduleUseCase = updateScrapingScheduleUseCase;
		this.runScheduledScrapingUseCase = runScheduledScrapingUseCase;
		this.listMarketSegmentsUseCase = listMarketSegmentsUseCase;
	}

	@GetMapping
	public String scrapingPage(@RequestParam(required = false, defaultValue = "run") String tab, Model model) {
		populatePage(model);
		model.addAttribute("activeTab", normalizeTab(tab));
		model.addAttribute("pageTitle", "Scraping");
		model.addAttribute("activeNav", "scraping");
		model.addAttribute("pageDescription", "Ejecutar búsquedas, configurar sesiones y programar tareas");
		return "scraping/index";
	}

	@PostMapping(value = "/run", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String runScraping(
			@RequestParam(required = false) Long profileId,
			@RequestParam(required = false) String keywords,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) java.math.BigDecimal salaryMin,
			@RequestParam(required = false) java.math.BigDecimal salaryMax,
			@RequestParam(required = false) String employmentType,
			@RequestParam(required = false) String workMode,
			@RequestParam(required = false) Integer postedWithinDays,
			@RequestParam(required = false) List<String> platforms,
			@RequestParam(required = false) Integer maxResults,
			@RequestParam(required = false) Long segmentId,
			RedirectAttributes redirectAttributes) {
		try {
			List<JobPlatform> resolvedPlatforms = resolvePlatforms(platforms);
			int resolvedMax = maxResults != null ? maxResults : getScrapingSettingsUseCase.execute().defaultMaxResults();

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
					segmentId));

			redirectAttributes.addFlashAttribute("scrapeResult", result);
			redirectAttributes.addFlashAttribute(
					"successMessage",
					"Búsqueda completada: " + result.imported() + " nuevas, "
							+ result.duplicates() + " duplicadas, "
							+ result.scraped() + " encontradas.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/scraping?tab=run";
	}

	@PostMapping("/settings")
	public String updateSettings(
			@RequestParam int rateLimitMs,
			@RequestParam int defaultMaxResults,
			@RequestParam(required = false) List<String> defaultPlatforms,
			@RequestParam(required = false) String linkedinStorageStatePath,
			@RequestParam int linkedinPageTimeoutMs,
			@RequestParam(required = false) String occStorageStatePath,
			@RequestParam int occPageTimeoutMs,
			@RequestParam(required = false) String indeedStorageStatePath,
			@RequestParam int indeedPageTimeoutMs,
			@RequestParam(required = false) String computrabajoStorageStatePath,
			@RequestParam int computrabajoPageTimeoutMs,
			@RequestParam(defaultValue = "false") boolean schedulingEnabled,
			@RequestParam long schedulingPollIntervalMs,
			RedirectAttributes redirectAttributes) {
		try {
			updateScrapingSettingsUseCase.execute(new UpdateScrapingSettingsUseCase.UpdateScrapingSettingsCommand(
					rateLimitMs,
					defaultMaxResults,
					resolvePlatforms(defaultPlatforms),
					linkedinStorageStatePath,
					linkedinPageTimeoutMs,
					occStorageStatePath,
					occPageTimeoutMs,
					indeedStorageStatePath,
					indeedPageTimeoutMs,
					computrabajoStorageStatePath,
					computrabajoPageTimeoutMs,
					schedulingEnabled,
					schedulingPollIntervalMs));
			redirectAttributes.addFlashAttribute("successMessage", "Configuración guardada.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/scraping?tab=settings";
	}

	@PostMapping("/profiles")
	public String createProfile(
			@RequestParam String name,
			@RequestParam String keywords,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) java.math.BigDecimal salaryMin,
			@RequestParam(required = false) java.math.BigDecimal salaryMax,
			@RequestParam(required = false) String employmentType,
			@RequestParam(required = false) String workMode,
			@RequestParam(required = false) Integer postedWithinDays,
			RedirectAttributes redirectAttributes) {
		try {
			ScrapeFilterSet filters = new ScrapeFilterSet(
					location, salaryMin, salaryMax, employmentType, workMode, postedWithinDays);
			String filtersJson = ScrapeFilterSetParser.toJson(filters);
			createSearchProfileUseCase.execute(
					new CreateSearchProfileUseCase.CreateSearchProfileCommand(name, keywords, filtersJson));
			redirectAttributes.addFlashAttribute("successMessage", "Perfil creado.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/scraping?tab=profiles";
	}

	@PostMapping("/schedules")
	public String createSchedule(
			@RequestParam Long profileId,
			@RequestParam(required = false) List<String> platforms,
			@RequestParam int intervalMinutes,
			@RequestParam int maxResults,
			RedirectAttributes redirectAttributes) {
		try {
			createScrapingScheduleUseCase.execute(new CreateScrapingScheduleUseCase.CreateScrapingScheduleCommand(
					profileId, resolvePlatforms(platforms), intervalMinutes, maxResults));
			redirectAttributes.addFlashAttribute("successMessage", "Schedule creado.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/scraping?tab=schedules";
	}

	@PostMapping("/schedules/{id}/toggle")
	public String toggleSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			ScrapingSchedule schedule = listScrapingSchedulesUseCase.execute().stream()
					.filter(item -> item.id().equals(id))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));
			updateScrapingScheduleUseCase.execute(
					id,
					new UpdateScrapingScheduleUseCase.UpdateScrapingScheduleCommand(!schedule.enabled()));
			redirectAttributes.addFlashAttribute("successMessage", "Schedule actualizado.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/scraping?tab=schedules";
	}

	@PostMapping("/schedules/{id}/run")
	public String runSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			RunScheduledScrapingUseCase.ScheduleRunResult result = runScheduledScrapingUseCase.executeById(id);
			redirectAttributes.addFlashAttribute(
					"successMessage",
					"Schedule ejecutado: " + result.imported() + " nuevas, "
							+ result.duplicates() + " duplicadas.");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/scraping?tab=schedules";
	}

	private void populatePage(Model model) {
		ScrapingSettings settings = getScrapingSettingsUseCase.execute();
		List<SearchProfile> profiles = listSearchProfilesUseCase.execute();
		List<ProfileView> profileViews = profiles.stream()
				.map(profile -> new ProfileView(
						profile,
						ScrapeFilterFormatter.toLabels(profile.filters())))
				.toList();
		Map<Long, SearchProfile> profilesById =
				profiles.stream().collect(Collectors.toMap(SearchProfile::id, Function.identity()));

		List<ScheduleView> schedules = listScrapingSchedulesUseCase.execute().stream()
				.map(schedule -> new ScheduleView(
						schedule,
						profilesById.containsKey(schedule.profileId())
								? profilesById.get(schedule.profileId()).name()
								: "Perfil #" + schedule.profileId()))
				.toList();

		boolean linkedinSessionConfigured = isConfigured(settings.linkedinStorageStatePath());
		boolean occSessionConfigured = isConfigured(settings.occStorageStatePath());
		boolean indeedSessionConfigured = isConfigured(settings.indeedStorageStatePath());
		boolean computrabajoSessionConfigured = isConfigured(settings.computrabajoStorageStatePath());
		long activeSchedules = schedules.stream().filter(sv -> sv.schedule().enabled()).count();

		model.addAttribute("settings", settings);
		model.addAttribute("profiles", profiles);
		model.addAttribute("segments", listMarketSegmentsUseCase.execute());
		model.addAttribute("profileViews", profileViews);
		model.addAttribute("schedules", schedules);
		model.addAttribute("allPlatforms", Arrays.asList(JobPlatform.values()));
		model.addAttribute("linkedinSessionConfigured", linkedinSessionConfigured);
		model.addAttribute("occSessionConfigured", occSessionConfigured);
		model.addAttribute("indeedSessionConfigured", indeedSessionConfigured);
		model.addAttribute("computrabajoSessionConfigured", computrabajoSessionConfigured);
		model.addAttribute("activeSchedules", activeSchedules);
	}

	private static boolean isConfigured(String path) {
		return path != null && !path.isBlank();
	}

	private static String normalizeTab(String tab) {
		return switch (tab) {
			case "settings", "profiles", "schedules" -> tab;
			default -> "run";
		};
	}

	private List<JobPlatform> resolvePlatforms(List<String> platforms) {
		if (platforms == null || platforms.isEmpty()) {
			return new ArrayList<>(getScrapingSettingsUseCase.execute().defaultPlatforms());
		}
		return platforms.stream().map(JobPlatform::fromSource).toList();
	}

	public record ScheduleView(ScrapingSchedule schedule, String profileName) {
	}

	public record ProfileView(SearchProfile profile, List<String> filterLabels) {
	}
}
