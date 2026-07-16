package com.projects.knowledge_manager.dashboard.dto;

import com.projects.knowledge_manager.behavioral.dto.BehavioralStatsView;
import com.projects.knowledge_manager.review.dto.ReviewView;
import java.util.List;

public record DashboardView(
    List<DueProblemView> dueToday,
    long dueTodayTotal,
    long dueTodayBacklog,
    int dueTodayLimit,
    List<DueProblemView> upcoming,
    long upcomingTotal,
    List<DueProblemView> newQueue,
    long newQueueTotal,
    List<ReviewView> recentReviews,
    LearningStatsView stats,
    BehavioralStatsView behavioralStats,
    List<TopicProgressView> topicProgress,
    HeatmapView heatmap) {}
