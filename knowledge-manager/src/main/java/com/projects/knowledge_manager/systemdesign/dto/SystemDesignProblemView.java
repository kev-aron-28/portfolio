package com.projects.knowledge_manager.systemdesign.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SystemDesignProblemView(
    Long id,
    String title,
    SystemDesignCategory category,
    Difficulty difficulty,
    String description,
    String originalSource,
    Integer estimatedInterviewTime,
    boolean favorite,
    List<String> tags,
    String whiteboardJson,
    String overview,
    String functionalRequirements,
    String nonFunctionalRequirements,
    String assumptions,
    String highLevelArchitecture,
    String components,
    String databaseDesign,
    String apiDesign,
    String scalingStrategy,
    String cachingStrategy,
    String loadBalancing,
    String messaging,
    String tradeoffs,
    String bottlenecks,
    String lessonsLearned,
    String personalNotes,
    LocalDate nextReviewDate,
    boolean dueToday,
    boolean overdue,
    boolean neverReviewed,
    Instant createdAt,
    Instant updatedAt) {}
