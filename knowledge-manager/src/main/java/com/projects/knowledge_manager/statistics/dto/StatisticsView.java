package com.projects.knowledge_manager.statistics.dto;

import java.util.List;

public record StatisticsView(
    String schedulerName,
    long totalReviewTimeMinutes,
    long currentStreak,
    long longestStreak,
    List<RatingStatView> ratingStats,
    List<MonthStatView> monthlyStats,
    long masteredProblems,
    long problemsWithoutReview) {}
