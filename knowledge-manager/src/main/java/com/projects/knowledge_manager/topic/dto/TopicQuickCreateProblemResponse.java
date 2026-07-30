package com.projects.knowledge_manager.topic.dto;

/** Catalog card payload after quick-creating a problem from a topic form. */
public record TopicQuickCreateProblemResponse(
    Long id, String title, String difficulty, String topicNames) {}
