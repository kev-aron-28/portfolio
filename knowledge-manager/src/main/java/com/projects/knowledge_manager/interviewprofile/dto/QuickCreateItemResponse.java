package com.projects.knowledge_manager.interviewprofile.dto;

/** Lightweight catalog card returned after quick-creating a study item from a profile form. */
public record QuickCreateItemResponse(
    Long id,
    String title,
    String meta,
    String group,
    String badgeLabel,
    String badgeClass,
    String fieldName) {}
