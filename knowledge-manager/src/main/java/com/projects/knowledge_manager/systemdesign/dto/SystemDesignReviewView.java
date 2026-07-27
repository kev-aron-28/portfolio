package com.projects.knowledge_manager.systemdesign.dto;

import java.time.LocalDate;

public record SystemDesignReviewView(
    Long id,
    Long problemId,
    String problemTitle,
    LocalDate reviewDate,
    int durationSeconds,
    int rating,
    LocalDate nextReviewDate) {}
