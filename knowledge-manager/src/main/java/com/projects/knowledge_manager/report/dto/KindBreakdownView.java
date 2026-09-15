package com.projects.knowledge_manager.report.dto;

public record KindBreakdownView(
    PracticeKind kind, long sessions, long durationMinutes, double averageRating, int barWidth) {}
