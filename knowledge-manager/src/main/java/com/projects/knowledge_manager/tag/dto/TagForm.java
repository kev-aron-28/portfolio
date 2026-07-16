package com.projects.knowledge_manager.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagForm(
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    String name) {

  public static TagForm empty() {
    return new TagForm("");
  }
}
