package com.projects.knowledge_manager.statistics.dto;

public record TopicHealthView(
    Long topicId,
    String name,
    String color,
    long totalProblems,
    long mastered,
    long learning,
    long weak,
    long failing,
    long untouched,
    long overdue,
    int coveragePercent,
    int masteryPercent,
    double averageRating,
    long totalMinutes,
    String health,
    String recommendation) {}
