package com.projects.knowledge_manager.interviewprofile.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import java.time.Instant;
import java.util.List;

public record InterviewProfileView(
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
    List<Difficulty> targetDifficulties,
    List<Long> problemIds,
    List<Long> behavioralQuestionIds,
    List<Long> systemDesignProblemIds,
    int assignedProblemCount,
    int assignedBehavioralCount,
    int assignedSystemDesignCount,
    boolean archived,
    String extraSettingsJson,
    Instant createdAt,
    Instant updatedAt) {}
