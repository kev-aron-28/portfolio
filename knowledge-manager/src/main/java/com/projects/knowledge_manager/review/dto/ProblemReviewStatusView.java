package com.projects.knowledge_manager.review.dto;

import java.time.LocalDate;
import java.util.List;

public record ProblemReviewStatusView(
    LocalDate nextReviewDate,
    boolean dueToday,
    boolean overdue,
    boolean neverReviewed,
    List<ReviewView> history) {}
