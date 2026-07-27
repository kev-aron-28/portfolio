package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.time.LocalDate;

/** A selectable study item for interview generation. */
public record InterviewCandidate(
    QuestionType type,
    Long sourceId,
    String title,
    LocalDate nextReviewDate,
    boolean overdue,
    boolean due,
    boolean neverReviewed,
    Integer lastRating,
    int priorityScore) {}
