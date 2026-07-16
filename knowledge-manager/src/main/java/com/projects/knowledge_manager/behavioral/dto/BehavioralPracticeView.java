package com.projects.knowledge_manager.behavioral.dto;

import java.time.LocalDate;

public record BehavioralPracticeView(
    Long id,
    Long questionId,
    String questionTitle,
    LocalDate practiceDate,
    int durationSeconds,
    int rating,
    LocalDate nextReviewDate) {}
