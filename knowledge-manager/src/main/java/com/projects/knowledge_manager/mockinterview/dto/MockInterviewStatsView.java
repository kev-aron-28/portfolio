package com.projects.knowledge_manager.mockinterview.dto;

public record MockInterviewStatsView(
    long totalInterviews,
    long totalInterviewSeconds,
    double averageInterviewSeconds,
    double averageQuestionSeconds,
    long interviewsThisMonth,
    int longestInterviewSeconds,
    int fastestInterviewSeconds,
    long totalQuestionsReviewed) {}
