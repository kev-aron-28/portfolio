package com.projects.knowledge_manager.statistics.service;

import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.review.entity.Review;
import com.projects.knowledge_manager.review.repository.ReviewRepository;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.statistics.dto.MonthStatView;
import com.projects.knowledge_manager.statistics.dto.RatingStatView;
import com.projects.knowledge_manager.statistics.dto.StatisticsView;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

  private static final int MONTHS_TO_SHOW = 6;
  private static final int MASTERED_RATING_THRESHOLD = 4;
  private static final int MASTERED_STREAK_REQUIRED = 2;

  private final ReviewRepository reviewRepository;
  private final ProblemRepository problemRepository;
  private final ReviewService reviewService;
  private final String schedulerName;

  public StatisticsService(
      ReviewRepository reviewRepository,
      ProblemRepository problemRepository,
      ReviewService reviewService,
      @Value("${app.review.scheduler:sm2}") String schedulerName) {
    this.reviewRepository = reviewRepository;
    this.problemRepository = problemRepository;
    this.reviewService = reviewService;
    this.schedulerName = schedulerName;
  }

  public StatisticsView buildStatistics() {
    List<Review> reviews = reviewRepository.findAll();
    Set<LocalDate> reviewDates =
        reviews.stream().map(Review::getReviewDate).collect(Collectors.toSet());

    return new StatisticsView(
        schedulerName,
        reviews.stream().mapToLong(Review::getReviewDuration).sum(),
        calculateCurrentStreak(reviewDates, LocalDate.now()),
        calculateLongestStreak(reviewDates),
        buildRatingStats(reviews),
        buildMonthlyStats(reviews),
        countMasteredProblems(),
        countProblemsWithoutReview());
  }

  private List<RatingStatView> buildRatingStats(List<Review> reviews) {
    Map<Integer, Long> distribution = buildRatingDistribution(reviews);
    long max = distribution.values().stream().mapToLong(value -> value).max().orElse(1);

    return distribution.entrySet().stream()
        .map(
            entry ->
                new RatingStatView(
                    entry.getKey(),
                    entry.getValue(),
                    max == 0 ? 0 : (int) Math.round(entry.getValue() * 100.0 / max)))
        .toList();
  }

  private List<MonthStatView> buildMonthlyStats(List<Review> reviews) {
    Map<String, Long> monthlyCounts = buildMonthlyCounts(reviews);
    long max = monthlyCounts.values().stream().mapToLong(value -> value).max().orElse(1);

    return monthlyCounts.entrySet().stream()
        .map(
            entry ->
                new MonthStatView(
                    entry.getKey(),
                    entry.getValue(),
                    max == 0 ? 0 : (int) Math.round(entry.getValue() * 100.0 / max)))
        .toList();
  }

  private Map<Integer, Long> buildRatingDistribution(List<Review> reviews) {
    Map<Integer, Long> distribution = new TreeMap<>();
    for (int rating = 1; rating <= 5; rating++) {
      distribution.put(rating, 0L);
    }
    reviews.forEach(review -> distribution.merge(review.getRating(), 1L, Long::sum));
    return distribution;
  }

  private Map<String, Long> buildMonthlyCounts(List<Review> reviews) {
    Map<YearMonth, Long> counts = new TreeMap<>();
    LocalDate today = LocalDate.now();
    YearMonth startMonth = YearMonth.from(today.minusMonths(MONTHS_TO_SHOW - 1L));

    for (int i = 0; i < MONTHS_TO_SHOW; i++) {
      counts.put(startMonth.plusMonths(i), 0L);
    }

    reviews.stream()
        .map(review -> YearMonth.from(review.getReviewDate()))
        .filter(counts::containsKey)
        .forEach(month -> counts.merge(month, 1L, Long::sum));

    Map<String, Long> labels = new LinkedHashMap<>();
    counts.forEach(
        (month, count) ->
            labels.put(
                month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + month.getYear(),
                count));
    return labels;
  }

  private long countMasteredProblems() {
    return problemRepository.findAllByArchivedFalseOrderByTitleAsc().stream()
        .filter(
            problem -> {
              List<Review> history =
                  reviewRepository.findByProblemIdOrderByReviewDateDescIdDesc(problem.getId());
              if (history.size() < MASTERED_STREAK_REQUIRED) {
                return false;
              }
              return history.stream()
                  .limit(MASTERED_STREAK_REQUIRED)
                  .allMatch(review -> review.getRating() >= MASTERED_RATING_THRESHOLD);
            })
        .count();
  }

  private long countProblemsWithoutReview() {
    return problemRepository.findAllByArchivedFalseOrderByTitleAsc().stream()
        .filter(problem -> reviewService.findLatestReview(problem.getId()).isEmpty())
        .count();
  }

  private long calculateCurrentStreak(Set<LocalDate> reviewDates, LocalDate today) {
    long streak = 0;
    LocalDate cursor = today;
    while (reviewDates.contains(cursor)) {
      streak++;
      cursor = cursor.minusDays(1);
    }
    return streak;
  }

  private long calculateLongestStreak(Set<LocalDate> reviewDates) {
    if (reviewDates.isEmpty()) {
      return 0;
    }

    List<LocalDate> sortedDates = reviewDates.stream().sorted(Comparator.naturalOrder()).toList();
    long longest = 1;
    long current = 1;

    for (int i = 1; i < sortedDates.size(); i++) {
      if (sortedDates.get(i - 1).plusDays(1).equals(sortedDates.get(i))) {
        current++;
        longest = Math.max(longest, current);
      } else {
        current = 1;
      }
    }
    return longest;
  }
}
