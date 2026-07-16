package com.projects.knowledge_manager.behavioral.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BehavioralPracticeForm(
    @NotNull(message = "Practice date is required") LocalDate practiceDate,
    @NotNull(message = "Duration is required")
        @Min(value = 0, message = "Duration cannot be negative")
        Integer durationSeconds,
    @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating) {

  public static BehavioralPracticeForm empty() {
    return new BehavioralPracticeForm(LocalDate.now(), 0, null);
  }
}
