package com.projects.knowledge_manager.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ReviewForm(
    @NotNull(message = "Review date is required") LocalDate reviewDate,

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    Integer rating,

    @NotNull(message = "Duration is required")
    @Min(value = 0, message = "Duration cannot be negative")
    Integer reviewDuration,

    @Size(max = 5000, message = "Notes must be at most 5000 characters")
    String notes) {

  public static ReviewForm empty() {
    return new ReviewForm(LocalDate.now(), 3, 1, "");
  }
}
