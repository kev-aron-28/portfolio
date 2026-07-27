package com.projects.knowledge_manager.mockinterview.dto;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.time.Instant;
import java.util.List;

public record MockInterviewSummaryView(
    Long id,
    InterviewFormat format,
    Long profileId,
    String profileName,
    Instant startedAt,
    Instant finishedAt,
    int totalDurationSeconds,
    int totalQuestions,
    int behavioralCount,
    int algorithmCount,
    int systemDesignCount,
    double averageQuestionSeconds,
    List<MockInterviewItemView> items) {}
