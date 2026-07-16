package com.projects.knowledge_manager.review.service;

import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.problem.service.ProblemNotFoundException;
import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import com.projects.knowledge_manager.review.dto.ProblemReviewStatusView;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.dto.ReviewView;
import com.projects.knowledge_manager.review.entity.Review;
import com.projects.knowledge_manager.review.mapper.ReviewMapper;
import com.projects.knowledge_manager.review.repository.ReviewRepository;
import com.projects.knowledge_manager.review.scheduler.FirstReviewScheduleCalculator;
import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;
import com.projects.knowledge_manager.review.scheduler.ReviewResult;
import com.projects.knowledge_manager.review.scheduler.ReviewScheduler;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final ProblemRepository problemRepository;
  private final ReviewScheduler reviewScheduler;
  private final FirstReviewScheduleCalculator firstReviewScheduleCalculator;
  private final ReviewSchedulingProperties schedulingProperties;

  public ReviewService(
      ReviewRepository reviewRepository,
      ProblemRepository problemRepository,
      ReviewScheduler reviewScheduler,
      FirstReviewScheduleCalculator firstReviewScheduleCalculator,
      ReviewSchedulingProperties schedulingProperties) {
    this.reviewRepository = reviewRepository;
    this.problemRepository = problemRepository;
    this.reviewScheduler = reviewScheduler;
    this.firstReviewScheduleCalculator = firstReviewScheduleCalculator;
    this.schedulingProperties = schedulingProperties;
  }

  public List<ReviewView> findHistoryByProblemId(Long problemId) {
    ensureProblemExists(problemId);
    return reviewRepository.findByProblemIdOrderByReviewDateDescIdDesc(problemId).stream()
        .map(ReviewMapper::toView)
        .toList();
  }

  public ProblemReviewStatusView getProblemReviewStatus(Long problemId) {
    Problem problem = getProblemOrThrow(problemId);
    LocalDate today = LocalDate.now();
    boolean neverReviewed = isNeverReviewed(problemId);
    LocalDate nextReviewDate = resolveNextReviewDate(problem);
    List<ReviewView> history = findHistoryByProblemId(problemId);

    return new ProblemReviewStatusView(
        nextReviewDate,
        !nextReviewDate.isAfter(today),
        nextReviewDate.isBefore(today),
        neverReviewed,
        history);
  }

  @Transactional
  public ReviewView create(Long problemId, ReviewForm form) {
    Problem problem = getProblemOrThrow(problemId);
    List<ReviewHistoryEntry> history =
        reviewRepository.findByProblemIdOrderByReviewDateDescIdDesc(problemId).stream()
            .map(ReviewMapper::toHistoryEntry)
            .toList();

    ReviewResult result = new ReviewResult(form.reviewDate(), form.rating());
    LocalDate nextReviewDate = reviewScheduler.calculateNextReview(result, history);

    Review review =
        new Review(
            problem,
            form.reviewDate(),
            form.rating(),
            nextReviewDate,
            form.reviewDuration());
    review.setNotes(normalizeNotes(form.notes()));

    return ReviewMapper.toView(reviewRepository.save(review));
  }

  public LocalDate resolveNextReviewDate(Problem problem) {
    Optional<Review> latestReview = findLatestReview(problem.getId());
    if (latestReview.isPresent()) {
      return latestReview.get().getNextReviewDate();
    }
    return firstReviewScheduleCalculator.calculateFirstReviewDate(problem);
  }

  public boolean isNeverReviewed(Long problemId) {
    return findLatestReview(problemId).isEmpty();
  }

  public int getUpcomingWindowDays() {
    return schedulingProperties.getUpcomingWindowDays();
  }

  public Optional<Review> findLatestReview(Long problemId) {
    return reviewRepository.findByProblemIdOrderByReviewDateDescIdDesc(problemId).stream()
        .findFirst();
  }

  public Optional<Integer> findLatestRating(Long problemId) {
    return findLatestReview(problemId).map(Review::getRating);
  }

  public List<ReviewView> findRecentReviews(int limit) {
    return reviewRepository.findRecentReviews(org.springframework.data.domain.PageRequest.of(0, limit))
        .stream()
        .map(ReviewMapper::toView)
        .toList();
  }

  private Problem getProblemOrThrow(Long problemId) {
    return problemRepository
        .findDetailedById(problemId)
        .orElseThrow(() -> new ProblemNotFoundException(problemId));
  }

  private void ensureProblemExists(Long problemId) {
    if (!problemRepository.existsById(problemId)) {
      throw new ProblemNotFoundException(problemId);
    }
  }

  private String normalizeNotes(String notes) {
    if (notes == null || notes.isBlank()) {
      return null;
    }
    return notes.trim();
  }
}
