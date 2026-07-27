package com.projects.knowledge_manager.systemdesign.service;

import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import com.projects.knowledge_manager.dashboard.dto.HeatmapCellView;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;
import com.projects.knowledge_manager.review.scheduler.ReviewResult;
import com.projects.knowledge_manager.review.scheduler.ReviewScheduler;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemForm;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemSummaryView;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemView;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignReviewForm;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignReviewView;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignStatsView;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignStatsView.CategoryCountView;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignProblem;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignReview;
import com.projects.knowledge_manager.systemdesign.mapper.SystemDesignMapper;
import com.projects.knowledge_manager.systemdesign.model.ReviewStatusFilter;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import com.projects.knowledge_manager.systemdesign.repository.SystemDesignProblemRepository;
import com.projects.knowledge_manager.systemdesign.repository.SystemDesignReviewRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SystemDesignProblemService {

  private static final int HEATMAP_WEEKS = 26;
  private static final int MASTERED_MIN_REVIEWS = 3;
  private static final int MASTERED_MIN_RATING = PracticeRating.EASY.getQuality();

  private final SystemDesignProblemRepository problemRepository;
  private final SystemDesignReviewRepository reviewRepository;
  private final ReviewScheduler reviewScheduler;
  private final ReviewSchedulingProperties schedulingProperties;

  public SystemDesignProblemService(
      SystemDesignProblemRepository problemRepository,
      SystemDesignReviewRepository reviewRepository,
      ReviewScheduler reviewScheduler,
      ReviewSchedulingProperties schedulingProperties) {
    this.problemRepository = problemRepository;
    this.reviewRepository = reviewRepository;
    this.reviewScheduler = reviewScheduler;
    this.schedulingProperties = schedulingProperties;
  }

  public List<SystemDesignProblemSummaryView> findFiltered(
      SystemDesignCategory category,
      Difficulty difficulty,
      String tag,
      ReviewStatusFilter status,
      Boolean dueTodayOnly,
      Boolean favoritesOnly,
      String query) {
    LocalDate today = LocalDate.now();
    String normalizedQuery = query == null ? "" : query.trim();
    boolean favoriteOnly = Boolean.TRUE.equals(favoritesOnly);
    List<SystemDesignProblem> problems =
        problemRepository.search(category, difficulty, favoriteOnly, normalizedQuery);

    String normalizedTag = tag == null ? "" : tag.trim().toLowerCase(Locale.ROOT);
    ReviewStatusFilter effectiveStatus = status == null ? ReviewStatusFilter.ALL : status;
    boolean forceDueToday = Boolean.TRUE.equals(dueTodayOnly);

    List<SystemDesignProblemSummaryView> summaries = new ArrayList<>();
    for (SystemDesignProblem problem : problems) {
      if (!normalizedTag.isEmpty()
          && problem.getTags().stream()
              .noneMatch(value -> value.toLowerCase(Locale.ROOT).contains(normalizedTag))) {
        continue;
      }

      SystemDesignProblemSummaryView summary = toSummary(problem, today);
      if (forceDueToday && !summary.dueToday()) {
        continue;
      }
      if (!matchesStatus(summary, effectiveStatus)) {
        continue;
      }
      summaries.add(summary);
    }
    return summaries;
  }

  public SystemDesignProblemView findById(Long id) {
    SystemDesignProblem problem = getDetailedOrThrow(id);
    LocalDate today = LocalDate.now();
    return SystemDesignMapper.toView(
        problem, resolveNextReviewDate(id), today, isNeverReviewed(id));
  }

  public SystemDesignProblemForm findFormById(Long id) {
    return SystemDesignMapper.toForm(getDetailedOrThrow(id));
  }

  public List<SystemDesignReviewView> findReviewHistory(Long problemId) {
    ensureProblemExists(problemId);
    return reviewRepository.findByProblemIdOrderByReviewDateDescIdDesc(problemId).stream()
        .map(SystemDesignMapper::toReviewView)
        .toList();
  }

  public Optional<SystemDesignProblemView> findRandomDueProblem() {
    LocalDate today = LocalDate.now();
    List<SystemDesignProblem> due = new ArrayList<>();
    for (SystemDesignProblem problem : problemRepository.findAll()) {
      if (!resolveNextReviewDate(problem.getId()).isAfter(today)) {
        due.add(problem);
      }
    }
    if (due.isEmpty()) {
      return Optional.empty();
    }
    Collections.shuffle(due);
    SystemDesignProblem selected = due.getFirst();
    return Optional.of(
        SystemDesignMapper.toView(
            getDetailedOrThrow(selected.getId()),
            resolveNextReviewDate(selected.getId()),
            today,
            isNeverReviewed(selected.getId())));
  }

  public SystemDesignStatsView buildStats() {
    LocalDate today = LocalDate.now();
    List<SystemDesignProblem> problems = problemRepository.findAll();
    long dueToday =
        problems.stream()
            .filter(problem -> !resolveNextReviewDate(problem.getId()).isAfter(today))
            .count();

    Double averageDuration = reviewRepository.averageDurationSeconds();
    Double averageRating = reviewRepository.averageRating();

    List<CategoryCountView> mostReviewed =
        reviewRepository.countGroupedByCategory().stream()
            .map(
                row ->
                    new CategoryCountView(
                        ((SystemDesignCategory) row[0]).getLabel(), ((Number) row[1]).longValue()))
            .limit(5)
            .toList();

    Map<SystemDesignCategory, Long> mastered = new EnumMap<>(SystemDesignCategory.class);
    for (SystemDesignProblem problem : problems) {
      Optional<SystemDesignReview> latest = findLatestReview(problem.getId());
      long reviewCount = reviewRepository.countByProblemId(problem.getId());
      if (latest.isPresent()
          && reviewCount >= MASTERED_MIN_REVIEWS
          && latest.get().getRating() >= MASTERED_MIN_RATING
          && latest.get().getNextReviewDate().isAfter(today.plusDays(14))) {
        mastered.merge(problem.getCategory(), 1L, Long::sum);
      }
    }
    List<CategoryCountView> topicsMastered =
        mastered.entrySet().stream()
            .sorted(Map.Entry.<SystemDesignCategory, Long>comparingByValue().reversed())
            .map(entry -> new CategoryCountView(entry.getKey().getLabel(), entry.getValue()))
            .toList();

    return new SystemDesignStatsView(
        problems.size(),
        dueToday,
        reviewRepository.count(),
        averageDuration == null ? 0 : Math.round(averageDuration * 10.0) / 10.0,
        averageRating == null ? 0 : Math.round(averageRating * 10.0) / 10.0,
        mostReviewed,
        topicsMastered,
        buildHeatmap(today));
  }

  private List<List<HeatmapCellView>> buildHeatmap(LocalDate today) {
    LocalDate gridStart = today.minusWeeks(HEATMAP_WEEKS);
    while (gridStart.getDayOfWeek() != DayOfWeek.SUNDAY) {
      gridStart = gridStart.minusDays(1);
    }

    Map<LocalDate, Long> counts = new HashMap<>();
    for (Object[] row : reviewRepository.countReviewsGroupedByDate(gridStart)) {
      counts.put((LocalDate) row[0], ((Number) row[1]).longValue());
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
    return weeks;
  }

  @Transactional
  public SystemDesignProblemView create(SystemDesignProblemForm form) {
    SystemDesignProblem saved = problemRepository.save(SystemDesignMapper.toEntity(form));
    return SystemDesignMapper.toView(saved, LocalDate.now(), LocalDate.now(), true);
  }

  @Transactional
  public SystemDesignProblemView update(Long id, SystemDesignProblemForm form) {
    SystemDesignProblem problem = getDetailedOrThrow(id);
    SystemDesignMapper.updateEntity(problem, form);
    return SystemDesignMapper.toView(
        problem, resolveNextReviewDate(id), LocalDate.now(), isNeverReviewed(id));
  }

  @Transactional
  public void delete(Long id) {
    problemRepository.delete(getProblemOrThrow(id));
  }

  @Transactional
  public SystemDesignReviewView recordReview(Long problemId, SystemDesignReviewForm form) {
    SystemDesignProblem problem = getProblemOrThrow(problemId);
    validateRating(form.rating());

    List<ReviewHistoryEntry> history =
        reviewRepository.findByProblemIdOrderByReviewDateDescIdDesc(problemId).stream()
            .map(SystemDesignMapper::toHistoryEntry)
            .toList();

    ReviewResult result = new ReviewResult(form.reviewDate(), form.rating());
    LocalDate nextReviewDate = reviewScheduler.calculateNextReview(result, history);

    SystemDesignReview review =
        new SystemDesignReview(
            problem,
            form.reviewDate(),
            form.durationSeconds(),
            form.rating(),
            nextReviewDate);

    return SystemDesignMapper.toReviewView(reviewRepository.save(review));
  }

  @Transactional
  public void saveWhiteboard(Long problemId, String sceneJson) {
    SystemDesignProblem problem = getProblemOrThrow(problemId);
    if (sceneJson != null && sceneJson.length() > 2_000_000) {
      throw new IllegalArgumentException("Whiteboard scene is too large.");
    }
    problem.setWhiteboardJson(sceneJson == null || sceneJson.isBlank() ? null : sceneJson);
  }

  public LocalDate resolveNextReviewDate(Long problemId) {
    return findLatestReview(problemId)
        .map(SystemDesignReview::getNextReviewDate)
        .orElseGet(
            () ->
                getProblemOrThrow(problemId)
                    .getCreatedAt()
                    .atZone(schedulingProperties.getZoneId())
                    .toLocalDate());
  }

  public boolean isNeverReviewed(Long problemId) {
    return findLatestReview(problemId).isEmpty();
  }

  public Optional<Integer> findLatestRating(Long problemId) {
    return findLatestReview(problemId).map(SystemDesignReview::getRating);
  }

  private SystemDesignProblemSummaryView toSummary(SystemDesignProblem problem, LocalDate today) {
    Long id = problem.getId();
    Optional<SystemDesignReview> latest = findLatestReview(id);
    return SystemDesignMapper.toSummary(
        problem,
        resolveNextReviewDate(id),
        today,
        latest.isEmpty(),
        latest.map(SystemDesignReview::getRating).orElse(null),
        reviewRepository.countByProblemId(id));
  }

  private boolean matchesStatus(
      SystemDesignProblemSummaryView summary, ReviewStatusFilter status) {
    return switch (status) {
      case ALL -> true;
      case DUE_TODAY -> summary.dueToday() && !summary.overdue();
      case OVERDUE -> summary.overdue();
      case UPCOMING -> !summary.dueToday() && !summary.neverReviewed();
      case NEVER_REVIEWED -> summary.neverReviewed();
      case MASTERED ->
          summary.lastRating() != null
              && summary.lastRating() >= MASTERED_MIN_RATING
              && summary.reviewCount() >= MASTERED_MIN_REVIEWS
              && !summary.dueToday();
    };
  }

  private Optional<SystemDesignReview> findLatestReview(Long problemId) {
    return reviewRepository.findByProblemIdOrderByReviewDateDescIdDesc(problemId).stream()
        .findFirst();
  }

  private SystemDesignProblem getProblemOrThrow(Long id) {
    return problemRepository
        .findById(id)
        .orElseThrow(() -> new SystemDesignProblemNotFoundException(id));
  }

  private SystemDesignProblem getDetailedOrThrow(Long id) {
    return problemRepository
        .findDetailedById(id)
        .orElseThrow(() -> new SystemDesignProblemNotFoundException(id));
  }

  private void ensureProblemExists(Long id) {
    if (!problemRepository.existsById(id)) {
      throw new SystemDesignProblemNotFoundException(id);
    }
  }

  private void validateRating(Integer rating) {
    if (PracticeRating.fromQuality(rating) == null) {
      throw new IllegalArgumentException(
          "Rating must be one of Forgot(1), Hard(3), Good(4), or Easy(5).");
    }
  }
}
