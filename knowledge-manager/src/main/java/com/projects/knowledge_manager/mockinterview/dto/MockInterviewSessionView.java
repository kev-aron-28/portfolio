package com.projects.knowledge_manager.mockinterview.dto;

import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionView;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import com.projects.knowledge_manager.problem.dto.ProblemDetailView;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemView;

public record MockInterviewSessionView(
    Long interviewId,
    String formatLabel,
    int questionNumber,
    int totalQuestions,
    long interviewElapsedSeconds,
    MockInterviewItemView currentItem,
    QuestionType questionType,
    ProblemDetailView problem,
    BehavioralQuestionView behavioral,
    SystemDesignProblemView systemDesign,
    boolean revealAnswer) {}
