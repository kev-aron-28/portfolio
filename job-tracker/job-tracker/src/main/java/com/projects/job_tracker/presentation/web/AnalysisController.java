package com.projects.job_tracker.presentation.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projects.job_tracker.application.analytics.AnalyzeSearchReportUseCase;
import com.projects.job_tracker.domain.model.SearchReport;

@Controller
public class AnalysisController {

	private final AnalyzeSearchReportUseCase analyzeSearchReportUseCase;

	public AnalysisController(AnalyzeSearchReportUseCase analyzeSearchReportUseCase) {
		this.analyzeSearchReportUseCase = analyzeSearchReportUseCase;
	}

	@GetMapping("/analisis")
	public String analysis(Model model) {
		SearchReport report = analyzeSearchReportUseCase.execute();
		model.addAttribute("report", report);
		model.addAttribute("pageTitle", "Análisis");
		model.addAttribute("activeNav", "analysis");
		model.addAttribute("pageDescription", "Lectura de postulaciones, tiempos de espera y comparación con el mercado importado");
		return "analysis";
	}
}
