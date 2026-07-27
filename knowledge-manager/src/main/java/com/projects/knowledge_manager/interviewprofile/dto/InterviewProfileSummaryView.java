package com.projects.knowledge_manager.interviewprofile.dto;

import java.time.Instant;

public record InterviewProfileSummaryView(
    Long id,
    String name,
    String description,
    String company,
    String color,
    String icon,
    int behavioralQuestionCount,
    int algorithmQuestionCount,
    int systemDesignQuestionCount,
    Integer maxDurationMinutes,
    int assignedProblemCount,
    int assignedBehavioralCount,
    int assignedSystemDesignCount,
    boolean archived,
    Instant updatedAt) {}
