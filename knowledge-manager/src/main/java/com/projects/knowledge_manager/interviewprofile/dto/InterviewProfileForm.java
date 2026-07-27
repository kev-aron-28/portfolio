package com.projects.knowledge_manager.interviewprofile.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public record InterviewProfileForm(
    @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,
    @Size(max = 1000, message = "Description must be at most 1000 characters") String description,
    @Size(max = 100, message = "Company must be at most 100 characters") String company,
    @NotBlank(message = "Color is required")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a hex value like #2563eb")
        String color,
    @NotBlank(message = "Icon is required")
        @Size(max = 50, message = "Icon must be at most 50 characters")
        String icon,
    @Min(value = 0, message = "Behavioral count cannot be negative")
        @Max(value = 20, message = "Behavioral count must be at most 20")
        int behavioralQuestionCount,
    @Min(value = 0, message = "Algorithm count cannot be negative")
        @Max(value = 20, message = "Algorithm count must be at most 20")
        int algorithmQuestionCount,
    @Min(value = 0, message = "System design count cannot be negative")
        @Max(value = 20, message = "System design count must be at most 20")
        int systemDesignQuestionCount,
    @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 480, message = "Duration must be at most 480 minutes")
        Integer maxDurationMinutes,
    List<Difficulty> targetDifficulties,
    List<Long> problemIds,
    List<Long> behavioralQuestionIds,
    List<Long> systemDesignProblemIds,
    boolean archived,
    @Size(max = 5000, message = "Extra settings must be at most 5000 characters")
        String extraSettingsJson) {

  public InterviewProfileForm {
    targetDifficulties = targetDifficulties == null ? List.of() : List.copyOf(targetDifficulties);
    problemIds = problemIds == null ? List.of() : List.copyOf(problemIds);
    behavioralQuestionIds =
        behavioralQuestionIds == null ? List.of() : List.copyOf(behavioralQuestionIds);
    systemDesignProblemIds =
        systemDesignProblemIds == null ? List.of() : List.copyOf(systemDesignProblemIds);
  }

  public static InterviewProfileForm empty() {
    return new InterviewProfileForm(
        "",
        "",
        "",
        "#2563eb",
        "bi-briefcase",
        2,
        2,
        1,
        60,
        List.of(Difficulty.MEDIUM, Difficulty.HARD),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        false,
        "");
  }
}
