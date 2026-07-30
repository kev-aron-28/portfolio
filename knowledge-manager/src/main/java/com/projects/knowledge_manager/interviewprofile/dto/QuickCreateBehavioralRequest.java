package com.projects.knowledge_manager.interviewprofile.dto;

import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuickCreateBehavioralRequest(
    @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
    @NotNull(message = "Category is required") BehavioralCategory category,
    @NotBlank(message = "Question is required")
        @Size(max = 5000, message = "Question must be at most 5000 characters")
        String question) {}
