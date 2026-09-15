package com.projects.knowledge_manager.report.dto;

import java.time.LocalDate;
import java.util.List;

public record WeeklyReportView(
    LocalDate weekStart,
    LocalDate weekEnd,
    String rangeLabel,
    int weekOffset,
    boolean currentWeek,
    boolean hasActivity,
    long totalSessions,
    long totalMinutes,
    long activeDays,
    double averageRating,
    String headline,
    String summaryLine,
    List<DayActivityView> days,
    List<PracticeEntryView> highlights,
    List<KindBreakdownView> breakdown,
    List<TopicHighlightView> topTopics) {}
