package com.projects.knowledge_manager.dashboard.service;

import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionService;
import com.projects.knowledge_manager.dashboard.dto.DashboardView;
import com.projects.knowledge_manager.dashboard.dto.DueProblemView;
import com.projects.knowledge_manager.dashboard.dto.HeatmapCellView;
import com.projects.knowledge_manager.dashboard.dto.HeatmapView;
import com.projects.knowledge_manager.dashboard.dto.LearningStatsView;
import com.projects.knowledge_manager.dashboard.dto.TopicProgressView;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import com.projects.knowledge_manager.review.repository.ReviewRepository;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.topic.entity.Topic;
import com.projects.knowledge_manager.topic.repository.TopicRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

  private static final int HEATMAP_WEEKS = 26;
  private static final int RECENT_REVIEWS_LIMIT = 10;

  private final ProblemRepository problemRepository;
  private final ReviewRepository reviewRepository;
  private final TopicRepository topicRepository;
  private final ReviewService reviewService;
  private final BehavioralQuestionService behavioralQuestionService;
  private final ReviewSchedulingProperties schedulingProperties;

  public DashboardService(
      ProblemRepository problemRepository,
      ReviewRepository reviewRepository,
      TopicRepository topicRepository,
      ReviewService reviewService,
      BehavioralQuestionService behavioralQuestionService,
      ReviewSchedulingProperties schedulingProperties) {
    this.problemRepository = problemRepository;
    this.reviewRepository = reviewRepository;
    this.topicRepository = topicRepository;
    this.reviewService = reviewService;
    this.behavioralQuestionService = behavioralQuestionService;
    this.schedulingProperties = schedulingProperties;
  }

  public DashboardView buildDashboard() {
    LocalDate today = LocalDate.now();
    LocalDate upcomingLimit = today.plusDays(schedulingProperties.getUpcomingWindowDays());
    List<Problem> activeProblems = problemRepository.findAllByArchivedFalseOrderByTitleAsc();

    List<DueProblemView> dueTodayAll = new ArrayList<>();
    List<DueProblemView> upcomingAll = new ArrayList<>();
    List<DueProblemView> newQueueAll = new ArrayList<>();
    long neverReviewedCount = 0;

    for (Problem problem : activeProblems) {
      boolean neverReviewed = reviewService.isNeverReviewed(problem.getId());
      if (neverReviewed) {
        neverReviewedCount++;
      }

      LocalDate nextReviewDate = reviewService.resolveNextReviewDate(problem);
      DueProblemView dueProblem = toDueProblemView(problem, nextReviewDate, today, neverReviewed);

      if (!nextReviewDate.isAfter(today)) {
        dueTodayAll.add(dueProblem);
      } else if (neverReviewed) {
        if (!nextReviewDate.isAfter(upcomingLimit)) {
          newQueueAll.add(dueProblem);
        }
      } else if (!nextReviewDate.isAfter(upcomingLimit)) {
        upcomingAll.add(dueProblem);
      }
    }

    Comparator<DueProblemView> byNextReview =
        Comparator.comparing(DueProblemView::nextReviewDate)
            .thenComparing(DueProblemView::title, String.CASE_INSENSITIVE_ORDER);

    dueTodayAll.sort(
        Comparator.comparing(DueProblemView::overdue)
            .reversed()
            .thenComparing(byNextReview));
    upcomingAll.sort(byNextReview);
    newQueueAll.sort(byNextReview);

    int dueTodayLimit = schedulingProperties.getDashboardDueTodayLimit();
    List<DueProblemView> dueTodayQueue = limit(dueTodayAll, dueTodayLimit);
    long dueTodayBacklog = Math.max(0, dueTodayAll.size() - dueTodayQueue.size());

    return new DashboardView(
        dueTodayQueue,
        dueTodayAll.size(),
        dueTodayBacklog,
        dueTodayLimit,
        limit(upcomingAll, schedulingProperties.getDashboardUpcomingLimit()),
        upcomingAll.size(),
        limit(newQueueAll, schedulingProperties.getDashboardNewQueueLimit()),
        newQueueAll.size(),
        reviewService.findRecentReviews(RECENT_REVIEWS_LIMIT),
        buildStats(
            today,
            activeProblems.size(),
            dueTodayQueue.size(),
            upcomingAll.size(),
            neverReviewedCount),
        behavioralQuestionService.buildStats(),
        buildTopicProgress(activeProblems, today),
        buildHeatmap(today));
  }

  private List<DueProblemView> limit(List<DueProblemView> items, int maxItems) {
    if (items.size() <= maxItems) {
      return items;
    }
    return List.copyOf(items.subList(0, maxItems));
  }

  private DueProblemView toDueProblemView(
      Problem problem, LocalDate nextReviewDate, LocalDate today, boolean neverReviewed) {
    return new DueProblemView(
        problem.getId(),
        problem.getTitle(),
        problem.getTopic().getName(),
        problem.getTopic().getColor(),
        nextReviewDate,
        reviewService.findLatestRating(problem.getId()).orElse(null),
        nextReviewDate.isBefore(today),
        neverReviewed);
  }

  private LearningStatsView buildStats(
      LocalDate today,
      long activeProblems,
      long dueTodayCount,
      long upcomingCount,
      long neverReviewedCount) {
    LocalDate weekStart = today.minusDays(6);
    double averageRating =
        reviewRepository.findAll().stream().mapToInt(review -> review.getRating()).average().orElse(0);

    return new LearningStatsView(
        problemRepository.count(),
        activeProblems,
        reviewRepository.count(),
        reviewRepository.countByReviewDateBetween(weekStart, today),
        Math.round(averageRating * 10.0) / 10.0,
        dueTodayCount,
        upcomingCount,
        neverReviewedCount);
  }

  private List<TopicProgressView> buildTopicProgress(List<Problem> activeProblems, LocalDate today) {
    Map<Long, TopicAccumulator> accumulators = new HashMap<>();

    for (Topic topic : topicRepository.findAll()) {
      accumulators.put(topic.getId(), new TopicAccumulator(topic));
    }

    for (Problem problem : activeProblems) {
      TopicAccumulator accumulator = accumulators.get(problem.getTopic().getId());
      if (accumulator == null) {
        continue;
      }
      accumulator.totalProblems++;
      if (!reviewService.isNeverReviewed(problem.getId())) {
        accumulator.reviewedProblems++;
      }
      if (!reviewService.resolveNextReviewDate(problem).isAfter(today)) {
        accumulator.dueProblems++;
      }
    }

    return accumulators.values().stream()
        .filter(accumulator -> accumulator.totalProblems > 0)
        .sorted(Comparator.comparing(accumulator -> accumulator.topic.getName().toLowerCase()))
        .map(TopicAccumulator::toView)
        .toList();
  }

  private HeatmapView buildHeatmap(LocalDate today) {
    LocalDate gridStart = today.minusWeeks(HEATMAP_WEEKS);
    while (gridStart.getDayOfWeek() != DayOfWeek.SUNDAY) {
      gridStart = gridStart.minusDays(1);
    }

    Map<LocalDate, Long> counts = new HashMap<>();
    for (Object[] row : reviewRepository.countReviewsGroupedByDate(gridStart)) {
      LocalDate date = (LocalDate) row[0];
      long count = ((Number) row[1]).longValue();
      counts.put(date, count);
    }

    List<List<HeatmapCellView>> weeks = new ArrayList<>();
    LocalDate weekStart = gridStart;
    while (!weekStart.isAfter(today)) {
      List<HeatmapCellView> weekColumn = new ArrayList<>();
      for (int day = 0; day < 7; day++) {
        LocalDate date = weekStart.plusDays(day);
        if (date.isAfter(today)) {
          weekColumn.add(HeatmapCellView.empty());
        } else {
          weekColumn.add(new HeatmapCellView(date, counts.getOrDefault(date, 0L)));
        }
      }
      weeks.add(weekColumn);
      weekStart = weekStart.plusWeeks(1);
    }

    return new HeatmapView(weeks);
  }

  private static final class TopicAccumulator {
    private final Topic topic;
    private long totalProblems;
    private long reviewedProblems;
    private long dueProblems;

    private TopicAccumulator(Topic topic) {
      this.topic = topic;
    }

    private TopicProgressView toView() {
      return new TopicProgressView(
          topic.getId(),
          topic.getName(),
          topic.getColor(),
          totalProblems,
          reviewedProblems,
          dueProblems);
    }
  }
}
