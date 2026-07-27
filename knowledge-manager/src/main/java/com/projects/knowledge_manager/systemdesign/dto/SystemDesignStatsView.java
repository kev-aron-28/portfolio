package com.projects.knowledge_manager.systemdesign.dto;

import com.projects.knowledge_manager.dashboard.dto.HeatmapCellView;
import java.util.List;

public record SystemDesignStatsView(
    long totalDesigns,
    long dueTodayCount,
    long totalReviews,
    double averageReviewSeconds,
    double averageConfidence,
    List<CategoryCountView> mostReviewedTopics,
    List<CategoryCountView> topicsMastered,
    List<List<HeatmapCellView>> heatmapWeeks) {

  public record CategoryCountView(String category, long count) {}
}
