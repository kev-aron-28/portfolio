package com.projects.knowledge_manager.report.dto;

import java.time.LocalDate;
import java.util.List;

public record DayActivityView(
    LocalDate date,
    String dayLabel,
    String shortLabel,
    boolean today,
    boolean future,
    int sessionCount,
    int durationMinutes,
    int intensity,
    List<PracticeEntryView> entries) {}
