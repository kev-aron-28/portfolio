package com.projects.knowledge_manager.statistics.dto;

import java.util.List;

public record TimeAnalysisView(
    String headline,
    String summary,
    long minutesLast7Days,
    long minutesPrev7Days,
    Integer delta7Percent,
    long minutesLast30Days,
    long minutesPrev30Days,
    Integer delta30Percent,
    double averageSessionMinutes,
    long activeDaysLast30,
    double averageMinutesOnActiveDays,
    String consistencyLabel,
    String trendLabel,
    String busiestWeekday,
    List<TimeByTopicView> timeByTopic,
    List<AnalysisProblemView> slowStruggles,
    List<AnalysisProblemView> efficientMastery,
    List<AnalysisInsightView> insights) {}
