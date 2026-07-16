package com.projects.knowledge_manager.problem.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public record BulkProblemRowForm(
    @Size(max = 200, message = "Title must be at most 200 characters")
    String title,

    @Size(max = 500, message = "URL must be at most 500 characters")
    String url,

    Long topicId,

    Difficulty difficulty,

    @Size(max = 10000, message = "Description must be at most 10000 characters")
    String description,

    @Size(max = 50, message = "Language must be at most 50 characters")
    String language,

    @Size(max = 50000, message = "Source code must be at most 50000 characters")
    String sourceCode,

    List<Long> tagIds,

    @Size(max = 500, message = "New tag names must be at most 500 characters")
    String newTagNames) {

  public static BulkProblemRowForm empty() {
    return new BulkProblemRowForm(
        "", "", null, Difficulty.MEDIUM, "", "java", "", new ArrayList<>(), "");
  }
}
