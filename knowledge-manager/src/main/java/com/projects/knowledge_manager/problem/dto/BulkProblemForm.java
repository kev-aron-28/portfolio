package com.projects.knowledge_manager.problem.dto;

import jakarta.validation.Valid;
import java.util.List;

public record BulkProblemForm(@Valid List<BulkProblemRowForm> rows) {

  public static BulkProblemForm empty() {
    return new BulkProblemForm(List.of(BulkProblemRowForm.empty(), BulkProblemRowForm.empty()));
  }
}
