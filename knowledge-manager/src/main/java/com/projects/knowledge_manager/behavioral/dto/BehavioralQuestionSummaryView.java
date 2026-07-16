package com.projects.knowledge_manager.behavioral.dto;

import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import java.time.LocalDate;

public record BehavioralQuestionSummaryView(
    Long id,
    String title,
    BehavioralCategory category,
    LocalDate nextReviewDate,
    boolean dueToday,
    boolean overdue,
    boolean neverPracticed,
    Integer lastRating) {}
