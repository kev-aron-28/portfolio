package com.projects.knowledge_manager.dashboard.dto;

public record LearningStatsView(
    long totalProblems,
    long activeProblems,
    long totalReviews,
    long reviewsThisWeek,
    double averageRating,
    long dueTodayCount,
    long upcomingCount,
    long neverReviewedCount) {}
