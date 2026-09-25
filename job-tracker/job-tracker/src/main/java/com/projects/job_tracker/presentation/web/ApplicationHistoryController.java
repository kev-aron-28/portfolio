package com.projects.job_tracker.presentation.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projects.job_tracker.application.analytics.GetApplicationHistoryUseCase;
import com.projects.job_tracker.domain.model.ApplicationHistory;

@Controller
public class ApplicationHistoryController {

	private final GetApplicationHistoryUseCase getApplicationHistoryUseCase;

	public ApplicationHistoryController(GetApplicationHistoryUseCase getApplicationHistoryUseCase) {
		this.getApplicationHistoryUseCase = getApplicationHistoryUseCase;
	}

	@GetMapping("/historial")
	public String history(Model model) {
		ApplicationHistory history = getApplicationHistoryUseCase.execute();
		model.addAttribute("history", history);
		model.addAttribute("pageTitle", "Historial");
		model.addAttribute("activeNav", "history");
		model.addAttribute("pageDescription", "Línea de tiempo de las empresas y vacantes a las que has postulado");
		return "history";
	}
}
