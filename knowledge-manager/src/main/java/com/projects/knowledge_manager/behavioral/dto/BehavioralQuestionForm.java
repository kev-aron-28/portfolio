package com.projects.knowledge_manager.behavioral.dto;

import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BehavioralQuestionForm(
    @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
    @NotNull(message = "Category is required") BehavioralCategory category,
    @NotBlank(message = "Question is required")
        @Size(max = 5000, message = "Question must be at most 5000 characters")
        String question,
    @Size(max = 5000, message = "Situation must be at most 5000 characters") String answerSituation,
    @Size(max = 5000, message = "Task must be at most 5000 characters") String answerTask,
    @Size(max = 5000, message = "Action must be at most 5000 characters") String answerAction,
    @Size(max = 5000, message = "Result must be at most 5000 characters") String answerResult,
    @Size(max = 5000, message = "Notes must be at most 5000 characters") String notes) {

  public static BehavioralQuestionForm empty() {
    return new BehavioralQuestionForm(
        "", BehavioralCategory.TEAMWORK, "", "", "", "", "", "");
  }
}
