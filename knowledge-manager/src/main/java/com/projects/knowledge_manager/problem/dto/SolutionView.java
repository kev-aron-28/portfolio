package com.projects.knowledge_manager.problem.dto;

public record SolutionView(
    String language, String sourceCode, String explanation, String complexity, String mistakes) {}
