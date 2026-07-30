package com.projects.knowledge_manager.review.mapper;

import com.projects.knowledge_manager.review.dto.ReviewView;
import com.projects.knowledge_manager.review.entity.Review;
import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;
import com.projects.knowledge_manager.topic.dto.TopicRefView;
import com.projects.knowledge_manager.topic.mapper.TopicMapper;
import java.util.stream.Collectors;

public final class ReviewMapper {

  private ReviewMapper() {}

  public static ReviewView toView(Review review) {
    var topics = TopicMapper.toRefViews(review.getProblem().getTopics());
    return new ReviewView(
        review.getId(),
        review.getProblem().getId(),
        review.getProblem().getTitle(),
        topics.stream().map(TopicRefView::name).collect(Collectors.joining(", ")),
        topics.isEmpty() ? "#94a3b8" : topics.getFirst().color(),
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
