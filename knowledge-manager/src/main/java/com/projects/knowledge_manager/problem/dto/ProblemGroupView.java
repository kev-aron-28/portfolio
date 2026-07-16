package com.projects.knowledge_manager.problem.dto;

import java.util.List;

public record ProblemGroupView(
    Long id, String name, String color, List<ProblemSummaryView> problems) {

  public int problemCount() {
    return problems.size();
  }
}
