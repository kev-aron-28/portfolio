package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.QuestionType;

/** One planned question slot produced by an InterviewGenerator. */
public record InterviewPlanItem(QuestionType type, Long sourceId, String title) {}
