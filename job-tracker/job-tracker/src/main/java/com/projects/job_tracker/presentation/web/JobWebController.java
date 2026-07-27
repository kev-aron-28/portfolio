package com.projects.job_tracker.presentation.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import com.projects.job_tracker.application.analytics.GetJobDetailUseCase;
import com.projects.job_tracker.application.analytics.JobDetailPresenter;
import com.projects.job_tracker.application.analytics.JobListingPresenter;
import com.projects.job_tracker.application.analytics.ListJobListingsUseCase;
import com.projects.job_tracker.application.application.CreateApplicationUseCase;
import com.projects.job_tracker.application.job.CreateJobUseCase;
import com.projects.job_tracker.application.segment.ListMarketSegmentsUseCase;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.Job;
import com.projects.job_tracker.domain.model.JobDetail;
import com.projects.job_tracker.domain.model.JobGroupField;
import com.projects.job_tracker.domain.model.JobListViewMode;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobListingOverview;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.JobSortField;
import com.projects.job_tracker.domain.model.SortDirection;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@Controller
@RequestMapping("/jobs")
public class JobWebController {

	private final ListJobListingsUseCase listJobListingsUseCase;
	private final GetJobDetailUseCase getJobDetailUseCase;
	private final CreateApplicationUseCase createApplicationUseCase;
	private final CreateJobUseCase createJobUseCase;
	private final ListMarketSegmentsUseCase listMarketSegmentsUseCase;
	private final MarketSegmentRepository marketSegmentRepository;

	public JobWebController(
			ListJobListingsUseCase listJobListingsUseCase,
			GetJobDetailUseCase getJobDetailUseCase,
			CreateApplicationUseCase createApplicationUseCase,
			CreateJobUseCase createJobUseCase,
			ListMarketSegmentsUseCase listMarketSegmentsUseCase,
			MarketSegmentRepository marketSegmentRepository) {
		this.listJobListingsUseCase = listJobListingsUseCase;
		this.getJobDetailUseCase = getJobDetailUseCase;
		this.createApplicationUseCase = createApplicationUseCase;
		this.createJobUseCase = createJobUseCase;
		this.listMarketSegmentsUseCase = listMarketSegmentsUseCase;
		this.marketSegmentRepository = marketSegmentRepository;
	}

	@GetMapping(produces = MediaType.TEXT_HTML_VALUE)
	public String listJobs(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String source,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) String companyName,
			@RequestParam(required = false) BigDecimal minSalary,
			@RequestParam(required = false) BigDecimal maxSalary,
			@RequestParam(required = false) String workMode,
			@RequestParam(required = false) String employmentType,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) ApplicationStatus applicationStatus,
			@RequestParam(required = false, defaultValue = "false") boolean onlyUnapplied,
			@RequestParam(required = false) Long segmentId,
			@RequestParam(required = false) String sortBy,
			@RequestParam(required = false) String sortDirection,
			@RequestParam(required = false) String groupBy,
			@RequestParam(required = false) String view,
			Model model) {
		ListJobListingsUseCase.JobListingQuery query = new ListJobListingsUseCase.JobListingQuery(
				keyword,
				source,
				location,
				companyName,
				minSalary,
				maxSalary,
				workMode,
				employmentType,
				category,
				applicationStatus,
				onlyUnapplied,
				segmentId,
				JobSortField.fromParam(sortBy),
				SortDirection.fromParam(sortDirection));
		List<JobListing> jobs = listJobListingsUseCase.execute(query);
		JobGroupField groupField = JobGroupField.fromParam(groupBy);
		JobListViewMode viewMode = JobListViewMode.fromParam(view);
		JobListingOverview overview = JobListingPresenter.present(jobs, groupField);

		model.addAttribute("jobs", jobs);
		model.addAttribute("overview", overview);
		model.addAttribute("groupBy", groupField);
		model.addAttribute("viewMode", viewMode);
		model.addAttribute("groupFields", Arrays.asList(JobGroupField.values()));
		model.addAttribute("keyword", keyword);
		model.addAttribute("source", source);
		model.addAttribute("location", location);
		model.addAttribute("companyName", companyName);
		model.addAttribute("minSalary", minSalary);
		model.addAttribute("maxSalary", maxSalary);
		model.addAttribute("workMode", workMode);
		model.addAttribute("employmentType", employmentType);
		model.addAttribute("category", category);
		model.addAttribute("applicationStatus", applicationStatus);
		model.addAttribute("onlyUnapplied", onlyUnapplied);
		model.addAttribute("segmentId", segmentId);
		model.addAttribute("sortBy", JobSortField.fromParam(sortBy));
		model.addAttribute("sortDirection", SortDirection.fromParam(sortDirection));
		model.addAttribute("sources", Arrays.asList(JobPlatform.values()));
		model.addAttribute("statuses", Arrays.asList(ApplicationStatus.values()));
		model.addAttribute("sortFields", Arrays.asList(JobSortField.values()));
		model.addAttribute("pageTitle", "Vacantes");
		model.addAttribute("activeNav", "jobs");
		model.addAttribute("pageDescription", overview.total() + " resultado" + (overview.total() != 1 ? "s" : ""));
		return "jobs/list";
	}

	@GetMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE)
	public String newJobForm(
			@RequestParam(required = false) Long segmentId,
			Model model) {
		model.addAttribute("sources", Arrays.asList(JobPlatform.values()));
		model.addAttribute("segments", listMarketSegmentsUseCase.execute());
		model.addAttribute("selectedSegmentId", segmentId);
		model.addAttribute("pageTitle", "Nueva vacante");
		model.addAttribute("activeNav", "jobs");
		model.addAttribute("breadcrumbSection", "jobs");
		model.addAttribute("pageDescription", "Registrar una vacante de forma manual");
		return "jobs/form";
	}

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createJob(
			@RequestParam String title,
			@RequestParam String companyName,
			@RequestParam(required = false) String companyWebsite,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) BigDecimal salaryMin,
			@RequestParam(required = false) BigDecimal salaryMax,
			@RequestParam String source,
			@RequestParam String url,
			@RequestParam(required = false) String externalId,
			@RequestParam(required = false) String postedAt,
			@RequestParam(required = false) String employmentType,
			@RequestParam(required = false) String workMode,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String subcategory,
			@RequestParam(required = false) String benefits,
			@RequestParam(required = false) String requirements,
			@RequestParam(required = false) List<Long> segmentIds,
			RedirectAttributes redirectAttributes) {
		try {
			Job created = createJobUseCase.execute(new CreateJobUseCase.CreateJobCommand(
					title.trim(),
					companyName.trim(),
					blankToNull(companyWebsite),
					blankToNull(description),
					blankToNull(location),
					salaryMin,
					salaryMax,
					source.trim(),
					url.trim(),
					blankToNull(externalId),
					parsePostedAt(postedAt),
					blankToNull(employmentType),
					blankToNull(workMode),
					blankToNull(category),
					blankToNull(subcategory),
					blankToNull(benefits),
					blankToNull(requirements)));
			attachToSegments(created.id(), segmentIds);
			redirectAttributes.addFlashAttribute("successMessage", "Vacante creada.");
			return "redirect:/jobs/" + created.id();
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
			return "redirect:/jobs/new";
		}
	}

	private void attachToSegments(Long jobId, List<Long> segmentIds) {
		if (segmentIds == null || segmentIds.isEmpty()) {
			return;
		}
		for (Long segmentId : segmentIds) {
			if (segmentId != null) {
				marketSegmentRepository.attachJob(segmentId, jobId);
			}
		}
	}

	@GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
	public String jobDetail(@PathVariable Long id, Model model) {
		JobDetail detail = getJobDetailUseCase.execute(id);
		JobDetailPresenter.JobDetailView view = JobDetailPresenter.present(detail);
		model.addAttribute("detail", detail);
		model.addAttribute("jobView", view);
		model.addAttribute("statuses", Arrays.asList(ApplicationStatus.values()));
		model.addAttribute("pageTitle", detail.job().title());
		model.addAttribute("activeNav", "jobs");
		model.addAttribute("breadcrumbSection", "jobs");
		model.addAttribute("pageDescription", view.subtitle());
		model.addAttribute("detailLayout", true);
		return "jobs/detail";
	}

	@PostMapping("/{id}/apply")
	public String apply(
			@PathVariable Long id,
			@RequestParam ApplicationStatus status,
			@RequestParam(required = false) String notes) {
		createApplicationUseCase.execute(new CreateApplicationUseCase.CreateApplicationCommand(id, status, null, notes));
		return "redirect:/jobs/" + id;
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

	private static Instant parsePostedAt(String postedAt) {
		if (postedAt == null || postedAt.isBlank()) {
			return null;
		}
		return LocalDateTime.parse(postedAt.trim()).atZone(ZoneId.systemDefault()).toInstant();
	}
}
