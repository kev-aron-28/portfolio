package com.projects.knowledge_manager.vision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VisionBoardForm(
    @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
    @Size(max = 500, message = "Description must be at most 500 characters") String description,
    boolean useDemoTemplate) {

  public static VisionBoardForm empty() {
    return new VisionBoardForm("", "", true);
  }
}
