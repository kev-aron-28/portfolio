package com.projects.knowledge_manager.dashboard.dto;

public record TopicProgressView(
    Long topicId,
    String topicName,
    String topicColor,
    long totalProblems,
    long reviewedProblems,
    long dueProblems) {}
