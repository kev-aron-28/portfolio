package com.projects.knowledge_manager.review.dto;

import java.time.Instant;
import java.time.LocalDate;

public record ReviewView(
    Long id,
    Long problemId,
    String problemTitle,
    String topicName,
    String topicColor,
    LocalDate reviewDate,
    int rating,
    String notes,
    LocalDate nextReviewDate,
    int reviewDuration,
    Instant createdAt) {}
