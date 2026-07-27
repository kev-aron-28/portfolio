package com.projects.knowledge_manager.systemdesign.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import java.time.LocalDate;
import java.util.List;

public record SystemDesignProblemSummaryView(
    Long id,
    String title,
    SystemDesignCategory category,
    Difficulty difficulty,
    boolean favorite,
    List<String> tags,
    LocalDate nextReviewDate,
    boolean dueToday,
    boolean overdue,
    boolean neverReviewed,
    Integer lastRating,
    long reviewCount) {}
