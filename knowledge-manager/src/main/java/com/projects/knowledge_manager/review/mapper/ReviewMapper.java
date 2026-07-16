package com.projects.knowledge_manager.review.mapper;

import com.projects.knowledge_manager.review.dto.ReviewView;
import com.projects.knowledge_manager.review.entity.Review;
import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;

public final class ReviewMapper {

  private ReviewMapper() {}

  public static ReviewView toView(Review review) {
    return new ReviewView(
        review.getId(),
        review.getProblem().getId(),
        review.getProblem().getTitle(),
        review.getProblem().getTopic().getName(),
        review.getProblem().getTopic().getColor(),
        review.getReviewDate(),
        review.getRating(),
        review.getNotes(),
        review.getNextReviewDate(),
        review.getReviewDuration(),
        review.getCreatedAt());
  }

  public static ReviewHistoryEntry toHistoryEntry(Review review) {
    return new ReviewHistoryEntry(
        review.getReviewDate(), review.getRating(), review.getNextReviewDate());
  }
}
