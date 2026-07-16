package com.projects.knowledge_manager.dashboard.dto;

import java.time.LocalDate;

public record DueProblemView(
    Long problemId,
    String title,
    String topicName,
    String topicColor,
    LocalDate nextReviewDate,
    Integer lastRating,
    boolean overdue,
    boolean firstReview) {}
