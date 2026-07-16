package com.projects.knowledge_manager.review.scheduler;

import java.time.LocalDate;
import java.util.List;

public class FixedIntervalReviewScheduler implements ReviewScheduler {

  @Override
  public LocalDate calculateNextReview(ReviewResult result, List<ReviewHistoryEntry> history) {
    int days =
        switch (result.rating()) {
          case 1, 2 -> 1;
          case 3 -> 3;
          case 4 -> 7;
          default -> 14;
        };
    return result.reviewDate().plusDays(days);
  }
}
