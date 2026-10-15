package com.projects.knowledge_manager.statistics.dto;

import java.util.List;

public record KnowledgeAnalysisView(
    String headline,
    String summary,
    long dominatedCount,
    long weakCount,
    long failingCount,
    long gapCount,
    long overdueCount,
    List<TopicHealthView> dominatedTopics,
    List<TopicHealthView> focusTopics,
    List<TopicHealthView> gapTopics,
    List<AnalysisProblemView> studyNext,
    List<AnalysisProblemView> failingProblems,
    List<AnalysisProblemView> dominatedProblems,
    List<AnalysisInsightView> insights) {}
