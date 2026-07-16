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

import com.projects.job_tracker.application.analytics.GetJobDetailUseCase;
import com.projects.job_tracker.application.analytics.JobDetailPresenter;
import com.projects.job_tracker.application.analytics.JobListingPresenter;
import com.projects.job_tracker.application.analytics.ListJobListingsUseCase;
import com.projects.job_tracker.application.application.CreateApplicationUseCase;
import com.projects.job_tracker.domain.model.ApplicationStatus;
import com.projects.job_tracker.domain.model.JobDetail;
import com.projects.job_tracker.domain.model.JobGroupField;
import com.projects.job_tracker.domain.model.JobListViewMode;
import com.projects.job_tracker.domain.model.JobListing;
import com.projects.job_tracker.domain.model.JobListingOverview;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.JobSortField;
import com.projects.job_tracker.domain.model.SortDirection;

@Controller
@RequestMapping("/jobs")
public class JobWebController {

	private final ListJobListingsUseCase listJobListingsUseCase;
	private final GetJobDetailUseCase getJobDetailUseCase;
	private final CreateApplicationUseCase createApplicationUseCase;

	public JobWebController(
			ListJobListingsUseCase listJobListingsUseCase,
			GetJobDetailUseCase getJobDetailUseCase,
			CreateApplicationUseCase createApplicationUseCase) {
		this.listJobListingsUseCase = listJobListingsUseCase;
		this.getJobDetailUseCase = getJobDetailUseCase;
		this.createApplicationUseCase = createApplicationUseCase;
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
}
