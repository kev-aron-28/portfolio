package com.projects.knowledge_manager.problem.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public record ProblemForm(
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    String title,

    @Size(max = 500, message = "URL must be at most 500 characters")
    String url,

    @NotNull(message = "Difficulty is required") Difficulty difficulty,

    @Size(max = 10000, message = "Description must be at most 10000 characters")
    String description,

    @NotNull(message = "Topic is required") Long topicId,

    List<Long> tagIds,

    @Size(max = 500, message = "New tag names must be at most 500 characters")
    String newTagNames,

    boolean favorite,
    boolean archived,

    @NotBlank(message = "Language is required")
    @Size(max = 50, message = "Language must be at most 50 characters")
    String language,

    @Size(max = 50000, message = "Source code must be at most 50000 characters")
    String sourceCode,

    @Size(max = 10000, message = "Explanation must be at most 10000 characters")
    String explanation,

    @Size(max = 5000, message = "Complexity must be at most 5000 characters")
    String complexity,

    @Size(max = 5000, message = "Mistakes must be at most 5000 characters")
    String mistakes) {

  public static ProblemForm empty() {
    return new ProblemForm(
        "",
        "",
        Difficulty.MEDIUM,
        "",
        null,
        new ArrayList<>(),
        "",
        false,
        false,
        "java",
        "",
        "",
        "",
        "");
  }
}
