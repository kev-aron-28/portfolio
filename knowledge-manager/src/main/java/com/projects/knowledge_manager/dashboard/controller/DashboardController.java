package com.projects.knowledge_manager.dashboard.controller;

import com.projects.knowledge_manager.dashboard.service.DashboardService;
import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

  private final DashboardService dashboardService;
  private final ReviewSchedulingProperties schedulingProperties;

  public DashboardController(
      DashboardService dashboardService, ReviewSchedulingProperties schedulingProperties) {
    this.dashboardService = dashboardService;
    this.schedulingProperties = schedulingProperties;
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    model.addAttribute("dashboard", dashboardService.buildDashboard());
    model.addAttribute("newProblemSpreadDays", schedulingProperties.getNewProblemSpreadDays());
    model.addAttribute("pageTitle", "Dashboard");
    return "dashboard/index";
  }
}
