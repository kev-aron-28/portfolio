package com.projects.knowledge_manager.topic.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TopicQuickCreateProblemRequest(
    @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
    @NotNull(message = "Difficulty is required") Difficulty difficulty,
    @Size(max = 500, message = "URL must be at most 500 characters") String url,
    @Size(max = 10000, message = "Description must be at most 10000 characters")
        String description) {}
