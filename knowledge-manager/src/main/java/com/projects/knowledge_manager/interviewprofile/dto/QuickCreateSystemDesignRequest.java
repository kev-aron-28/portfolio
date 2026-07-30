package com.projects.knowledge_manager.interviewprofile.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuickCreateSystemDesignRequest(
    @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
    @NotNull(message = "Category is required") SystemDesignCategory category,
    @NotNull(message = "Difficulty is required") Difficulty difficulty,
    @NotBlank(message = "Prompt is required")
        @Size(max = 10000, message = "Prompt must be at most 10000 characters")
        String description) {}
