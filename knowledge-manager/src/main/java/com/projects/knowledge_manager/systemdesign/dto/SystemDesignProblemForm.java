package com.projects.knowledge_manager.systemdesign.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SystemDesignProblemForm(
    @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
    @NotNull(message = "Category is required") SystemDesignCategory category,
    @NotNull(message = "Difficulty is required") Difficulty difficulty,
    @NotBlank(message = "Description is required")
        @Size(max = 10000, message = "Description must be at most 10000 characters")
        String description,
    @Size(max = 500, message = "Original source must be at most 500 characters")
        String originalSource,
    @Min(value = 1, message = "Estimated interview time must be at least 1 minute")
        @Max(value = 300, message = "Estimated interview time must be at most 300 minutes")
        Integer estimatedInterviewTime,
    boolean favorite,
    @Size(max = 500, message = "Tags must be at most 500 characters") String tags,
    @Size(max = 20000) String overview,
    @Size(max = 20000) String functionalRequirements,
    @Size(max = 20000) String nonFunctionalRequirements,
    @Size(max = 20000) String assumptions,
    @Size(max = 20000) String highLevelArchitecture,
    @Size(max = 20000) String components,
    @Size(max = 20000) String databaseDesign,
    @Size(max = 20000) String apiDesign,
    @Size(max = 20000) String scalingStrategy,
    @Size(max = 20000) String cachingStrategy,
    @Size(max = 20000) String loadBalancing,
    @Size(max = 20000) String messaging,
    @Size(max = 20000) String tradeoffs,
    @Size(max = 20000) String bottlenecks,
    @Size(max = 20000) String lessonsLearned,
    @Size(max = 20000) String personalNotes) {

  public static SystemDesignProblemForm empty() {
    return new SystemDesignProblemForm(
        "",
        SystemDesignCategory.DISTRIBUTED_SYSTEMS,
        Difficulty.MEDIUM,
        "",
        "",
        45,
        false,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "");
  }
}
