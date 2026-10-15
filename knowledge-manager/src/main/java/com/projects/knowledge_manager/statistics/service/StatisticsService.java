package com.projects.knowledge_manager.statistics.service;

import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.review.entity.Review;
import com.projects.knowledge_manager.review.repository.ReviewRepository;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.statistics.dto.AnalysisInsightView;
import com.projects.knowledge_manager.statistics.dto.AnalysisProblemView;
import com.projects.knowledge_manager.statistics.dto.KnowledgeAnalysisView;
import com.projects.knowledge_manager.statistics.dto.MonthStatView;
import com.projects.knowledge_manager.statistics.dto.RatingStatView;
import com.projects.knowledge_manager.statistics.dto.StatisticsView;
import com.projects.knowledge_manager.statistics.dto.TimeAnalysisView;
import com.projects.knowledge_manager.statistics.dto.TimeByTopicView;
import com.projects.knowledge_manager.statistics.dto.TopicHealthView;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
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
  private static final int WEAK_RATING_THRESHOLD = 3;
  private static final int FAILING_RATING_THRESHOLD = 2;
  private static final int MAX_LIST = 8;
  private static final int MAX_TOPICS = 6;

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
    LocalDate today = LocalDate.now();
    List<Problem> problems = problemRepository.findAllByArchivedFalseOrderByTitleAsc();
    List<Review> reviews = reviewRepository.findAllWithActiveProblemsOrderByReviewDateDesc();
    Map<Long, List<Review>> reviewsByProblem = groupReviewsByProblem(reviews);
    List<ProblemSnapshot> snapshots =
        problems.stream().map(problem -> toSnapshot(problem, reviewsByProblem, today)).toList();

    Set<LocalDate> reviewDates =
        reviews.stream().map(Review::getReviewDate).collect(Collectors.toSet());

    return new StatisticsView(
        schedulerName,
        reviews.stream().mapToLong(Review::getReviewDuration).sum(),
        calculateCurrentStreak(reviewDates, today),
        calculateLongestStreak(reviewDates),
        buildRatingStats(reviews),
        buildMonthlyStats(reviews),
        snapshots.stream().filter(s -> s.status == ProblemStatus.DOMINATED).count(),
        snapshots.stream().filter(s -> s.status == ProblemStatus.UNTOUCHED).count(),
        buildKnowledgeAnalysis(snapshots),
        buildTimeAnalysis(reviews, snapshots, today));
  }

  private KnowledgeAnalysisView buildKnowledgeAnalysis(List<ProblemSnapshot> snapshots) {
    List<TopicHealthView> topicHealth = buildTopicHealth(snapshots);
    List<TopicHealthView> dominatedTopics =
        topicHealth.stream()
            .filter(t -> "Dominated".equals(t.health()))
            .limit(MAX_TOPICS)
            .toList();
    List<TopicHealthView> focusTopics =
        topicHealth.stream()
            .filter(t -> "Needs work".equals(t.health()) || "At risk".equals(t.health()))
            .sorted(
                Comparator.comparingInt(TopicHealthView::masteryPercent)
                    .thenComparing(
                        (a, b) ->
                            Long.compare(b.failing() + b.weak() + b.overdue(), a.failing() + a.weak() + a.overdue())))
            .limit(MAX_TOPICS)
            .toList();
    List<TopicHealthView> gapTopics =
        topicHealth.stream()
            .filter(t -> "Gap".equals(t.health()))
            .sorted(
                Comparator.comparingInt(TopicHealthView::coveragePercent)
                    .thenComparing(Comparator.comparingLong(TopicHealthView::untouched).reversed()))
            .limit(MAX_TOPICS)
            .toList();

    List<AnalysisProblemView> studyNext = buildStudyNext(snapshots);
    List<AnalysisProblemView> failing =
        snapshots.stream()
            .filter(s -> s.status == ProblemStatus.FAILING || s.status == ProblemStatus.WEAK)
            .sorted(
                Comparator.comparingInt((ProblemSnapshot s) -> s.latestRating == null ? 0 : s.latestRating)
                    .thenComparing(Comparator.comparingInt((ProblemSnapshot s) -> s.totalMinutes).reversed()))
            .limit(MAX_LIST)
            .map(s -> toProblemView(s, weaknessReason(s)))
            .toList();
    List<AnalysisProblemView> dominated =
        snapshots.stream()
            .filter(s -> s.status == ProblemStatus.DOMINATED)
            .sorted(Comparator.comparingInt((ProblemSnapshot s) -> s.totalMinutes))
            .limit(MAX_LIST)
            .map(s -> toProblemView(s, "Last reviews rated " + s.latestRating + "+"))
            .toList();

    long dominatedCount =
        snapshots.stream().filter(s -> s.status == ProblemStatus.DOMINATED).count();
    long weakCount = snapshots.stream().filter(s -> s.status == ProblemStatus.WEAK).count();
    long failingCount = snapshots.stream().filter(s -> s.status == ProblemStatus.FAILING).count();
    long gapCount = snapshots.stream().filter(s -> s.status == ProblemStatus.UNTOUCHED).count();
    long overdueCount = snapshots.stream().filter(s -> s.overdue).count();

    List<AnalysisInsightView> insights = new ArrayList<>();
    if (!focusTopics.isEmpty()) {
      TopicHealthView top = focusTopics.getFirst();
      insights.add(
          new AnalysisInsightView(
              "focus",
              "Priority topic: " + top.name(),
              top.recommendation()));
    }
    if (failingCount > 0) {
      insights.add(
          new AnalysisInsightView(
              "risk",
              failingCount + " problem" + (failingCount == 1 ? "" : "s") + " failing",
              "Re-work low-rated items before adding new ones — rating ≤ "
                  + FAILING_RATING_THRESHOLD
                  + " on the latest review."));
    }
    if (gapCount > 0) {
      insights.add(
          new AnalysisInsightView(
              "gap",
              gapCount + " untouched problem" + (gapCount == 1 ? "" : "s"),
              "Coverage gaps hide blind spots. Open never-reviewed items in weak topics first."));
    }
    if (overdueCount > 0) {
      insights.add(
          new AnalysisInsightView(
              "due",
              overdueCount + " overdue review" + (overdueCount == 1 ? "" : "s"),
              "Clear overdue reviews to protect retention on material you already started."));
    }
    if (!dominatedTopics.isEmpty() && insights.size() < 4) {
      insights.add(
          new AnalysisInsightView(
              "strength",
              "Strong in " + dominatedTopics.getFirst().name(),
              "Keep light maintenance reviews here; invest new study time in focus topics."));
    }
    if (insights.isEmpty()) {
      insights.add(
          new AnalysisInsightView(
              "start",
              "Not enough signal yet",
              "Log a few reviews across topics so the analysis can separate strengths from gaps."));
    }

    String headline = knowledgeHeadline(dominatedCount, failingCount, gapCount, overdueCount);
    String summary =
        dominatedCount
            + " dominated · "
            + (weakCount + failingCount)
            + " weak/failing · "
            + gapCount
            + " untouched · "
            + overdueCount
            + " overdue. Mastery = last "
            + MASTERED_STREAK_REQUIRED
            + " reviews rated ≥ "
            + MASTERED_RATING_THRESHOLD
            + ".";

    return new KnowledgeAnalysisView(
        headline,
        summary,
        dominatedCount,
        weakCount,
        failingCount,
        gapCount,
        overdueCount,
        dominatedTopics,
        focusTopics,
        gapTopics,
        studyNext,
        failing,
        dominated,
        insights);
  }

  private TimeAnalysisView buildTimeAnalysis(
      List<Review> reviews, List<ProblemSnapshot> snapshots, LocalDate today) {
    LocalDate d7 = today.minusDays(6);
    LocalDate prev7Start = today.minusDays(13);
    LocalDate prev7End = today.minusDays(7);
    LocalDate d30 = today.minusDays(29);
    LocalDate prev30Start = today.minusDays(59);
    LocalDate prev30End = today.minusDays(30);

    long minutesLast7 = minutesBetween(reviews, d7, today);
    long minutesPrev7 = minutesBetween(reviews, prev7Start, prev7End);
    long minutesLast30 = minutesBetween(reviews, d30, today);
    long minutesPrev30 = minutesBetween(reviews, prev30Start, prev30End);

    double avgSession =
        reviews.isEmpty()
            ? 0
            : Math.round(
                    reviews.stream().mapToInt(Review::getReviewDuration).average().orElse(0) * 10.0)
                / 10.0;

    Set<LocalDate> activeLast30 =
        reviews.stream()
            .map(Review::getReviewDate)
            .filter(date -> !date.isBefore(d30) && !date.isAfter(today))
            .collect(Collectors.toSet());
    long activeDays = activeLast30.size();
    double avgOnActive =
        activeDays == 0
            ? 0
            : Math.round((minutesLast30 / (double) activeDays) * 10.0) / 10.0;

    String consistency =
        activeDays >= 20
            ? "High consistency"
            : activeDays >= 12
                ? "Steady"
                : activeDays >= 5 ? "Irregular" : "Sparse";

    Integer delta7 = percentDelta(minutesLast7, minutesPrev7);
    Integer delta30 = percentDelta(minutesLast30, minutesPrev30);
    String trend =
        delta7 == null
            ? "No recent baseline"
            : delta7 > 10 ? "Time up vs prior week" : delta7 < -10 ? "Time down vs prior week" : "Time steady vs prior week";

    String busiest = busiestWeekday(reviews);
    List<TimeByTopicView> timeByTopic = buildTimeByTopic(snapshots);
    List<AnalysisProblemView> slowStruggles =
        snapshots.stream()
            .filter(s -> s.reviewCount > 0 && s.averageRating > 0 && s.averageRating <= 3.5)
            .filter(s -> s.avgMinutesPerReview >= Math.max(20, avgSession))
            .sorted(
                Comparator.comparingDouble((ProblemSnapshot s) -> s.avgMinutesPerReview).reversed())
            .limit(MAX_LIST)
            .map(
                s ->
                    toProblemView(
                        s,
                        Math.round(s.avgMinutesPerReview)
                            + " min/review · avg rating "
                            + s.averageRating))
            .toList();
    List<AnalysisProblemView> efficient =
        snapshots.stream()
            .filter(s -> s.status == ProblemStatus.DOMINATED)
            .filter(s -> s.avgMinutesPerReview > 0 && s.avgMinutesPerReview <= Math.max(12, avgSession))
            .sorted(Comparator.comparingDouble(s -> s.avgMinutesPerReview))
            .limit(MAX_LIST)
            .map(
                s ->
                    toProblemView(
                        s, Math.round(s.avgMinutesPerReview) + " min/review · solid recall"))
            .toList();

    List<AnalysisInsightView> insights = new ArrayList<>();
    if (delta7 != null) {
      insights.add(
          new AnalysisInsightView(
              "week",
              "Last 7 days: " + minutesLast7 + " min",
              delta7 >= 0
                  ? delta7 + "% vs the previous 7 days."
                  : Math.abs(delta7) + "% less than the previous 7 days."));
    }
    insights.add(
        new AnalysisInsightView(
            "pace",
            "Average session " + avgSession + " min",
            activeDays
                + " active day"
                + (activeDays == 1 ? "" : "s")
                + " in the last 30 · "
                + avgOnActive
                + " min on those days."));
    if (!slowStruggles.isEmpty()) {
      insights.add(
          new AnalysisInsightView(
              "slow",
              "Slow + weak: " + slowStruggles.getFirst().title(),
              "Long sessions with low ratings usually mean the approach is unclear — revisit patterns, not just answers."));
    }
    if (!timeByTopic.isEmpty()) {
      TimeByTopicView top = timeByTopic.getFirst();
      insights.add(
          new AnalysisInsightView(
              "invest",
              "Most time in " + top.name(),
              top.minutes()
                  + " min across "
                  + top.sessions()
                  + " session"
                  + (top.sessions() == 1 ? "" : "s")
                  + ". Check whether that investment matches your focus topics."));
    }

    String headline =
        minutesLast7 == 0 && minutesLast30 == 0
            ? "No study time logged recently"
            : trend;
    String summary =
        minutesLast7
            + " min this week · "
            + minutesLast30
            + " min last 30 days · avg session "
            + avgSession
            + " min · "
            + consistency.toLowerCase(Locale.ROOT)
            + ".";

    return new TimeAnalysisView(
        headline,
        summary,
        minutesLast7,
        minutesPrev7,
        delta7,
        minutesLast30,
        minutesPrev30,
        delta30,
        avgSession,
        activeDays,
        avgOnActive,
        consistency,
        trend,
        busiest,
        timeByTopic,
        slowStruggles,
        efficient,
        insights);
  }

  private List<TopicHealthView> buildTopicHealth(List<ProblemSnapshot> snapshots) {
    Map<Long, TopicAccumulator> byTopic = new HashMap<>();
    for (ProblemSnapshot snapshot : snapshots) {
      if (snapshot.topics.isEmpty()) {
        TopicAccumulator uncategorized =
            byTopic.computeIfAbsent(
                -1L, id -> new TopicAccumulator(-1L, "Uncategorized", "#94a3b8"));
        uncategorized.accept(snapshot);
        continue;
      }
      for (TopicRef topic : snapshot.topics) {
        TopicAccumulator acc =
            byTopic.computeIfAbsent(
                topic.id(), id -> new TopicAccumulator(topic.id(), topic.name(), topic.color()));
        acc.accept(snapshot);
      }
    }

    return byTopic.values().stream()
        .filter(acc -> acc.total > 0)
        .map(TopicAccumulator::toView)
        .sorted(
            Comparator.comparingInt(TopicHealthView::masteryPercent)
                .thenComparingLong(TopicHealthView::weak)
                .reversed()
                .thenComparing(TopicHealthView::name, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private List<AnalysisProblemView> buildStudyNext(List<ProblemSnapshot> snapshots) {
    List<AnalysisProblemView> next = new ArrayList<>();

    snapshots.stream()
        .filter(s -> s.overdue)
        .sorted(
            Comparator.comparing((ProblemSnapshot s) -> s.nextReviewDate)
                .thenComparingInt(s -> s.latestRating == null ? 0 : s.latestRating))
        .limit(4)
        .map(s -> toProblemView(s, "Overdue since " + s.nextReviewDate))
        .forEach(next::add);

    snapshots.stream()
        .filter(s -> s.status == ProblemStatus.FAILING || s.status == ProblemStatus.WEAK)
        .filter(s -> next.stream().noneMatch(v -> v.problemId().equals(s.problemId)))
        .sorted(Comparator.comparingInt((ProblemSnapshot s) -> s.latestRating == null ? 0 : s.latestRating))
        .limit(3)
        .map(s -> toProblemView(s, weaknessReason(s)))
        .forEach(next::add);

    snapshots.stream()
        .filter(s -> s.status == ProblemStatus.UNTOUCHED)
        .sorted(Comparator.comparing(s -> s.difficulty.ordinal()))
        .limit(3)
        .map(s -> toProblemView(s, "Never reviewed — close the coverage gap"))
        .forEach(
            view -> {
              if (next.size() < MAX_LIST
                  && next.stream().noneMatch(v -> v.problemId().equals(view.problemId()))) {
                next.add(view);
              }
            });

    return next.stream().limit(MAX_LIST).toList();
  }

  private List<TimeByTopicView> buildTimeByTopic(List<ProblemSnapshot> snapshots) {
    Map<String, long[]> totals = new HashMap<>();
    Map<String, String> colors = new HashMap<>();
    for (ProblemSnapshot snapshot : snapshots) {
      if (snapshot.totalMinutes <= 0) {
        continue;
      }
      if (snapshot.topics.isEmpty()) {
        long[] bucket = totals.computeIfAbsent("Uncategorized", key -> new long[2]);
        bucket[0] += snapshot.totalMinutes;
        bucket[1] += snapshot.reviewCount;
        colors.putIfAbsent("Uncategorized", "#94a3b8");
        continue;
      }
      for (TopicRef topic : snapshot.topics) {
        long[] bucket = totals.computeIfAbsent(topic.name(), key -> new long[2]);
        bucket[0] += snapshot.totalMinutes;
        bucket[1] += snapshot.reviewCount;
        colors.putIfAbsent(topic.name(), topic.color());
      }
    }
    long max = totals.values().stream().mapToLong(v -> v[0]).max().orElse(0);
    return totals.entrySet().stream()
        .sorted(Comparator.<Map.Entry<String, long[]>>comparingLong(e -> e.getValue()[0]).reversed())
        .limit(MAX_TOPICS)
        .map(
            entry ->
                new TimeByTopicView(
                    entry.getKey(),
                    colors.getOrDefault(entry.getKey(), "#94a3b8"),
                    entry.getValue()[0],
                    max == 0 ? 0 : (int) Math.round(entry.getValue()[0] * 100.0 / max),
                    entry.getValue()[1]))
        .toList();
  }

  private ProblemSnapshot toSnapshot(
      Problem problem, Map<Long, List<Review>> reviewsByProblem, LocalDate today) {
    List<Review> history =
        reviewsByProblem.getOrDefault(problem.getId(), List.of()).stream()
            .sorted(
                Comparator.comparing(Review::getReviewDate)
                    .reversed()
                    .thenComparing(Review::getId, Comparator.reverseOrder()))
            .toList();

    int reviewCount = history.size();
    int totalMinutes = history.stream().mapToInt(Review::getReviewDuration).sum();
    double averageRating =
        reviewCount == 0
            ? 0
            : Math.round(history.stream().mapToInt(Review::getRating).average().orElse(0) * 10.0)
                / 10.0;
    Integer latestRating = reviewCount == 0 ? null : history.getFirst().getRating();
    double avgMinutes = reviewCount == 0 ? 0 : totalMinutes / (double) reviewCount;
    LocalDate nextReview = reviewService.resolveNextReviewDate(problem);
    boolean overdue = reviewCount > 0 && nextReview.isBefore(today);
    boolean due = reviewCount > 0 && !nextReview.isAfter(today);

    ProblemStatus status = classify(history, averageRating, latestRating);

    List<TopicRef> topics =
        problem.getTopics().stream()
            .sorted(Comparator.comparing(topic -> topic.getName().toLowerCase(Locale.ROOT)))
            .map(topic -> new TopicRef(topic.getId(), topic.getName(), topic.getColor()))
            .toList();

    return new ProblemSnapshot(
        problem.getId(),
        problem.getTitle(),
        problem.getDifficulty(),
        topics,
        status,
        latestRating,
        averageRating,
        reviewCount,
        totalMinutes,
        avgMinutes,
        nextReview,
        overdue,
        due);
  }

  private ProblemStatus classify(
      List<Review> historyNewestFirst, double averageRating, Integer latestRating) {
    if (historyNewestFirst.isEmpty()) {
      return ProblemStatus.UNTOUCHED;
    }
    if (historyNewestFirst.size() >= MASTERED_STREAK_REQUIRED
        && historyNewestFirst.stream()
            .limit(MASTERED_STREAK_REQUIRED)
            .allMatch(review -> review.getRating() >= MASTERED_RATING_THRESHOLD)) {
      return ProblemStatus.DOMINATED;
    }
    if (latestRating != null && latestRating <= FAILING_RATING_THRESHOLD) {
      return ProblemStatus.FAILING;
    }
    if (historyNewestFirst.size() >= 2) {
      int newest = historyNewestFirst.get(0).getRating();
      int previous = historyNewestFirst.get(1).getRating();
      if (newest < previous && newest <= WEAK_RATING_THRESHOLD) {
        return ProblemStatus.FAILING;
      }
    }
    if (latestRating != null && latestRating <= WEAK_RATING_THRESHOLD
        || averageRating > 0 && averageRating < 3.5) {
      return ProblemStatus.WEAK;
    }
    return ProblemStatus.LEARNING;
  }

  private AnalysisProblemView toProblemView(ProblemSnapshot snapshot, String reason) {
    String topicLabel =
        snapshot.topics.isEmpty()
            ? "Uncategorized"
            : snapshot.topics.stream().map(TopicRef::name).collect(Collectors.joining(", "));
    return new AnalysisProblemView(
        snapshot.problemId,
        snapshot.title,
        topicLabel,
        snapshot.difficulty.name(),
        snapshot.latestRating,
        snapshot.averageRating,
        snapshot.reviewCount,
        snapshot.totalMinutes,
        reason,
        "/problems/" + snapshot.problemId);
  }

  private String weaknessReason(ProblemSnapshot snapshot) {
    if (snapshot.status == ProblemStatus.FAILING) {
      return "Latest rating "
          + snapshot.latestRating
          + " — rebuild the solution from first principles";
    }
    return "Avg rating "
        + snapshot.averageRating
        + " across "
        + snapshot.reviewCount
        + " review"
        + (snapshot.reviewCount == 1 ? "" : "s");
  }

  private String knowledgeHeadline(
      long dominated, long failing, long gaps, long overdue) {
    if (dominated + failing + gaps == 0) {
      return "No knowledge signal yet";
    }
    if (failing > dominated && failing > 0) {
      return "Stabilize weak spots before expanding";
    }
    if (overdue > 5) {
      return "Retention risk: clear the overdue queue";
    }
    if (gaps > dominated) {
      return "Coverage is the bottleneck";
    }
    if (dominated > 0 && failing == 0) {
      return "Strong base — raise the ceiling";
    }
    return "Mixed profile — protect strengths, close gaps";
  }

  private Map<Long, List<Review>> groupReviewsByProblem(List<Review> reviews) {
    Map<Long, List<Review>> map = new HashMap<>();
    for (Review review : reviews) {
      map.computeIfAbsent(review.getProblem().getId(), id -> new ArrayList<>()).add(review);
    }
    return map;
  }

  private long minutesBetween(List<Review> reviews, LocalDate start, LocalDate end) {
    return reviews.stream()
        .filter(review -> !review.getReviewDate().isBefore(start) && !review.getReviewDate().isAfter(end))
        .mapToLong(Review::getReviewDuration)
        .sum();
  }

  private Integer percentDelta(long current, long previous) {
    if (previous == 0) {
      return current == 0 ? 0 : null;
    }
    return (int) Math.round(((current - previous) * 100.0) / previous);
  }

  private String busiestWeekday(List<Review> reviews) {
    if (reviews.isEmpty()) {
      return "—";
    }
    Map<DayOfWeek, Long> minutes = new EnumMap<>(DayOfWeek.class);
    for (DayOfWeek day : DayOfWeek.values()) {
      minutes.put(day, 0L);
    }
    for (Review review : reviews) {
      minutes.merge(
          review.getReviewDate().getDayOfWeek(), (long) review.getReviewDuration(), Long::sum);
    }
    return minutes.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .filter(entry -> entry.getValue() > 0)
        .map(entry -> entry.getKey().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
        .orElse("—");
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
                month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " "
                    + month.getYear(),
                count));
    return labels;
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

  private enum ProblemStatus {
    DOMINATED,
    LEARNING,
    WEAK,
    FAILING,
    UNTOUCHED
  }

  private record TopicRef(Long id, String name, String color) {}

  private record ProblemSnapshot(
      Long problemId,
      String title,
      com.projects.knowledge_manager.problem.model.Difficulty difficulty,
      List<TopicRef> topics,
      ProblemStatus status,
      Integer latestRating,
      double averageRating,
      int reviewCount,
      int totalMinutes,
      double avgMinutesPerReview,
      LocalDate nextReviewDate,
      boolean overdue,
      boolean due) {}

  private static final class TopicAccumulator {
    private final Long id;
    private final String name;
    private final String color;
    private long total;
    private long mastered;
    private long learning;
    private long weak;
    private long failing;
    private long untouched;
    private long overdue;
    private long ratingPoints;
    private long ratedProblems;
    private long totalMinutes;

    private TopicAccumulator(Long id, String name, String color) {
      this.id = id;
      this.name = name;
      this.color = color;
    }

    private void accept(ProblemSnapshot snapshot) {
      total++;
      totalMinutes += snapshot.totalMinutes;
      if (snapshot.overdue) {
        overdue++;
      }
      if (snapshot.reviewCount > 0) {
        ratingPoints += Math.round(snapshot.averageRating * 10);
        ratedProblems++;
      }
      switch (snapshot.status) {
        case DOMINATED -> mastered++;
        case LEARNING -> learning++;
        case WEAK -> weak++;
        case FAILING -> failing++;
        case UNTOUCHED -> untouched++;
      }
    }

    private TopicHealthView toView() {
      int coverage = total == 0 ? 0 : (int) Math.round(((total - untouched) * 100.0) / total);
      int mastery = total == 0 ? 0 : (int) Math.round((mastered * 100.0) / total);
      double avg =
          ratedProblems == 0
              ? 0
              : Math.round((ratingPoints / (10.0 * ratedProblems)) * 10.0) / 10.0;

      String health;
      String recommendation;
      if (total > 0 && mastery >= 60 && failing + weak == 0) {
        health = "Dominated";
        recommendation = "Maintenance mode — light spaced reviews only.";
      } else if (untouched * 2 >= total && coverage < 40) {
        health = "Gap";
        recommendation = "Start with easier never-reviewed problems to build coverage.";
      } else if (failing + weak > mastered || overdue > 0 && (failing + weak) > 0) {
        health = "At risk";
        recommendation = "Fix failing/weak items and clear overdue reviews before adding new ones.";
      } else {
        health = "Needs work";
        recommendation = "Raise mastery with deliberate re-solves; target weak ratings first.";
      }

      return new TopicHealthView(
          id,
          name,
          color,
          total,
          mastered,
          learning,
          weak,
          failing,
          untouched,
          overdue,
          coverage,
          mastery,
          avg,
          totalMinutes,
          health,
          recommendation);
    }
  }
}
