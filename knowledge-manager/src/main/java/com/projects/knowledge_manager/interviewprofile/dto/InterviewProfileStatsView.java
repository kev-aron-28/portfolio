package com.projects.knowledge_manager.interviewprofile.dto;

import java.time.Instant;
import java.time.LocalDate;

public record InterviewProfileStatsView(
    Long profileId,
    String profileName,
    String company,
    String color,
    String icon,
    long interviewsCompleted,
    long questionsReviewed,
    double averageDurationSeconds,
    double averageRating,
    double masteryScore,
    long dueReviews,
    double completionPercentage,
    Instant lastInterviewAt,
    LocalDate lastInterviewDate) {}
