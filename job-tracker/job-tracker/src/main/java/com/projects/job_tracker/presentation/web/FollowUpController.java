package com.projects.job_tracker.presentation.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projects.job_tracker.application.analytics.AnalyzeApplicationFollowUpUseCase;
import com.projects.job_tracker.domain.model.FollowUpOverview;

@Controller
public class FollowUpController {

	private final AnalyzeApplicationFollowUpUseCase analyzeApplicationFollowUpUseCase;

	public FollowUpController(AnalyzeApplicationFollowUpUseCase analyzeApplicationFollowUpUseCase) {
		this.analyzeApplicationFollowUpUseCase = analyzeApplicationFollowUpUseCase;
	}

	@GetMapping("/seguimiento")
	public String followUp(Model model) {
		FollowUpOverview overview = analyzeApplicationFollowUpUseCase.execute();
		model.addAttribute("overview", overview);
		model.addAttribute("pageTitle", "Seguimiento");
		model.addAttribute("activeNav", "followup");
		model.addAttribute("pageDescription", "Vacantes por fecha registrada y estimación de respuesta según el tiempo en el mismo estado");
		return "follow-up";
	}
}
