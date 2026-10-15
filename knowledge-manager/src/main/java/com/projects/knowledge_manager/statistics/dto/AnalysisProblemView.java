package com.projects.knowledge_manager.statistics.dto;

public record AnalysisProblemView(
    Long problemId,
    String title,
    String topics,
    String difficulty,
    Integer latestRating,
    double averageRating,
    int reviewCount,
    int totalMinutes,
    String reason,
    String href) {}
