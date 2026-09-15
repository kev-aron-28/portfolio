package com.projects.knowledge_manager.report.controller;

import com.projects.knowledge_manager.report.service.WeeklyReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeeklyReportController {

  private final WeeklyReportService weeklyReportService;

  public WeeklyReportController(WeeklyReportService weeklyReportService) {
    this.weeklyReportService = weeklyReportService;
  }

  @GetMapping("/reports/weekly")
  public String weeklyReport(
      @RequestParam(name = "week", defaultValue = "0") int week, Model model) {
    var report = weeklyReportService.buildReport(week);
    model.addAttribute("report", report);
    model.addAttribute("pageTitle", "Weekly report");
    return "report/weekly";
  }
}
