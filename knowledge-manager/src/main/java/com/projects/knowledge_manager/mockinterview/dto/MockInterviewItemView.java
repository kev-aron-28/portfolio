package com.projects.knowledge_manager.mockinterview.dto;

import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.time.Instant;

public record MockInterviewItemView(
    Long id,
    int itemOrder,
    QuestionType questionType,
    Long problemId,
    Long behavioralQuestionId,
    Long systemDesignProblemId,
    String title,
    Integer durationSeconds,
    Integer rating,
    Instant completedAt,
    boolean completed) {}
