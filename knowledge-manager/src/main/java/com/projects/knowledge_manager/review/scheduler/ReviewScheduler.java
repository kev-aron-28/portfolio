package com.projects.knowledge_manager.review.scheduler;

import java.time.LocalDate;
import java.util.List;

public interface ReviewScheduler {

  LocalDate calculateNextReview(ReviewResult result, List<ReviewHistoryEntry> history);
}
