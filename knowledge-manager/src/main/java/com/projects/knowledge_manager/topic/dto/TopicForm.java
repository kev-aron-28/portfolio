package com.projects.knowledge_manager.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TopicForm(
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    String name,

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    String description,

    @NotBlank(message = "Color is required")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code")
    String color) {

  public static TopicForm empty() {
    return new TopicForm("", "", "#6366f1");
  }
}
