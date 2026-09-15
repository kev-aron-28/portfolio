package com.projects.knowledge_manager.report.dto;

import java.time.LocalDate;

public record PracticeEntryView(
    PracticeKind kind,
    LocalDate date,
    String title,
    String subtitle,
    Integer rating,
    int durationMinutes,
    String href,
    String accentColor) {}
