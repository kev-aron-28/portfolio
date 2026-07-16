package com.projects.job_tracker.presentation.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projects.job_tracker.application.analytics.GetDashboardMetricsUseCase;
import com.projects.job_tracker.application.analytics.ListJobListingsUseCase;
import com.projects.job_tracker.domain.model.DashboardMetrics;
import com.projects.job_tracker.domain.model.JobListing;

@Controller
public class DashboardController {

	private final GetDashboardMetricsUseCase getDashboardMetricsUseCase;
	private final ListJobListingsUseCase listJobListingsUseCase;

	public DashboardController(
			GetDashboardMetricsUseCase getDashboardMetricsUseCase,
			ListJobListingsUseCase listJobListingsUseCase) {
		this.getDashboardMetricsUseCase = getDashboardMetricsUseCase;
		this.listJobListingsUseCase = listJobListingsUseCase;
	}

	@GetMapping("/")
	public String dashboard(Model model) {
		DashboardMetrics metrics = getDashboardMetricsUseCase.execute();
		List<JobListing> recentJobs =
				listJobListingsUseCase.execute(ListJobListingsUseCase.JobListingQuery.empty()).stream()
						.limit(5)
						.toList();

		model.addAttribute("metrics", metrics);
		model.addAttribute("recentJobs", recentJobs);
		model.addAttribute("pageTitle", "Dashboard");
		model.addAttribute("activeNav", "dashboard");
		model.addAttribute("pageDescription", "Resumen de vacantes, postulaciones y panorama del mercado");
		return "dashboard";
	}
}
