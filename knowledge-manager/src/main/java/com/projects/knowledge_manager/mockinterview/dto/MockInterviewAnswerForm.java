package com.projects.knowledge_manager.mockinterview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MockInterviewAnswerForm(
    @NotNull(message = "Duration is required")
        @Min(value = 0, message = "Duration cannot be negative")
        Integer durationSeconds,
    @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating) {

  public static MockInterviewAnswerForm empty() {
    return new MockInterviewAnswerForm(0, null);
  }
}
