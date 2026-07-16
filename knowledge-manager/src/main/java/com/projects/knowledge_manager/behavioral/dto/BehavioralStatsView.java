package com.projects.knowledge_manager.behavioral.dto;

import java.time.LocalDate;

public record BehavioralStatsView(
    long totalQuestions,
    long dueTodayCount,
    long totalPractices,
    double averageDurationSeconds,
    LocalDate lastPracticeDate) {}
