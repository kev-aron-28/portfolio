package com.projects.knowledge_manager.behavioral.dto;

import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import java.time.Instant;
import java.time.LocalDate;

public record BehavioralQuestionView(
    Long id,
    String title,
    BehavioralCategory category,
    String question,
    String answerSituation,
    String answerTask,
    String answerAction,
    String answerResult,
    String notes,
    LocalDate nextReviewDate,
    boolean dueToday,
    boolean overdue,
    boolean neverPracticed,
    Instant createdAt,
    Instant updatedAt) {}
