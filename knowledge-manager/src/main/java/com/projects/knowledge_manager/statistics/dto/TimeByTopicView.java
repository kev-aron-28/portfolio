package com.projects.knowledge_manager.statistics.dto;

public record TimeByTopicView(
    String name, String color, long minutes, int barWidth, long sessions) {}
