package com.projects.knowledge_manager.report.service;

import com.projects.knowledge_manager.behavioral.entity.BehavioralPractice;
import com.projects.knowledge_manager.behavioral.repository.BehavioralPracticeRepository;
import com.projects.knowledge_manager.mockinterview.entity.MockInterview;
import com.projects.knowledge_manager.mockinterview.repository.MockInterviewRepository;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.report.dto.DayActivityView;
import com.projects.knowledge_manager.report.dto.KindBreakdownView;
import com.projects.knowledge_manager.report.dto.PracticeEntryView;
import com.projects.knowledge_manager.report.dto.PracticeKind;
import com.projects.knowledge_manager.report.dto.TopicHighlightView;
import com.projects.knowledge_manager.report.dto.WeeklyReportView;
import com.projects.knowledge_manager.review.entity.Review;
import com.projects.knowledge_manager.review.repository.ReviewRepository;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignReview;
import com.projects.knowledge_manager.systemdesign.repository.SystemDesignReviewRepository;
import com.projects.knowledge_manager.topic.entity.Topic;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WeeklyReportService {

  private static final DateTimeFormatter RANGE_DAY = DateTimeFormatter.ofPattern("MMM d");
  private static final DateTimeFormatter RANGE_DAY_YEAR = DateTimeFormatter.ofPattern("MMM d, yyyy");
  private static final int MAX_HIGHLIGHTS = 8;
  private static final int MAX_TOPICS = 6;

  private final ReviewRepository reviewRepository;
  private final BehavioralPracticeRepository behavioralPracticeRepository;
  private final SystemDesignReviewRepository systemDesignReviewRepository;
  private final MockInterviewRepository mockInterviewRepository;

  public WeeklyReportService(
      ReviewRepository reviewRepository,
      BehavioralPracticeRepository behavioralPracticeRepository,
      SystemDesignReviewRepository systemDesignReviewRepository,
      MockInterviewRepository mockInterviewRepository) {
    this.reviewRepository = reviewRepository;
    this.behavioralPracticeRepository = behavioralPracticeRepository;
    this.systemDesignReviewRepository = systemDesignReviewRepository;
    this.mockInterviewRepository = mockInterviewRepository;
  }

  public WeeklyReportView buildReport(int weekOffset) {
    return buildReport(LocalDate.now(), weekOffset);
  }

  WeeklyReportView buildReport(LocalDate today, int weekOffset) {
    int offset = Math.max(-52, Math.min(0, weekOffset));
    LocalDate weekStart =
        today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(offset);
    LocalDate weekEnd = weekStart.plusDays(6);
    boolean currentWeek = offset == 0;

    List<PracticeEntryView> entries = collectEntries(weekStart, weekEnd);
    Map<LocalDate, List<PracticeEntryView>> byDay = groupByDay(entries);
    List<DayActivityView> days = buildDays(weekStart, today, byDay);

    long totalSessions = entries.size();
    long totalMinutes = entries.stream().mapToLong(PracticeEntryView::durationMinutes).sum();
    long activeDays =
        days.stream().filter(day -> !day.future() && day.sessionCount() > 0).count();
    double averageRating = averageRating(entries);
    List<KindBreakdownView> breakdown = buildBreakdown(entries);
    List<TopicHighlightView> topTopics = buildTopTopics(entries);
    List<PracticeEntryView> highlights = pickHighlights(entries);

    return new WeeklyReportView(
        weekStart,
        weekEnd,
        formatRange(weekStart, weekEnd),
        offset,
        currentWeek,
        totalSessions > 0,
        totalSessions,
        totalMinutes,
        activeDays,
        averageRating,
        buildHeadline(totalSessions, activeDays, currentWeek),
        buildSummaryLine(totalSessions, totalMinutes, averageRating, breakdown),
        days,
        highlights,
        breakdown,
        topTopics);
  }

  private List<PracticeEntryView> collectEntries(LocalDate weekStart, LocalDate weekEnd) {
    List<PracticeEntryView> entries = new ArrayList<>();

    for (Review review :
        reviewRepository.findByReviewDateBetweenOrderByReviewDateDesc(weekStart, weekEnd)) {
      Problem problem = review.getProblem();
      String topics =
          problem.getTopics().stream()
              .sorted(Comparator.comparing(topic -> topic.getName().toLowerCase(Locale.ROOT)))
              .map(Topic::getName)
              .reduce((a, b) -> a + " · " + b)
              .orElse("Algorithms");
      String color =
          problem.getTopics().stream()
              .findFirst()
              .map(Topic::getColor)
              .orElse("#6366f1");
      entries.add(
          new PracticeEntryView(
              PracticeKind.ALGORITHM,
              review.getReviewDate(),
              problem.getTitle(),
              topics,
              review.getRating(),
              Math.max(0, review.getReviewDuration()),
              "/problems/" + problem.getId(),
              color));
    }

    for (BehavioralPractice practice :
        behavioralPracticeRepository.findByPracticeDateBetweenOrderByPracticeDateDesc(
            weekStart, weekEnd)) {
      entries.add(
          new PracticeEntryView(
              PracticeKind.BEHAVIORAL,
              practice.getPracticeDate(),
              practice.getQuestion().getTitle(),
              practice.getQuestion().getCategory().getLabel(),
              practice.getRating(),
              minutesFromSeconds(practice.getDurationSeconds()),
              "/behavioral/" + practice.getQuestion().getId(),
              "#0d9488"));
    }

    for (SystemDesignReview review :
        systemDesignReviewRepository.findByReviewDateBetweenOrderByReviewDateDesc(
            weekStart, weekEnd)) {
      entries.add(
          new PracticeEntryView(
              PracticeKind.SYSTEM_DESIGN,
              review.getReviewDate(),
              review.getProblem().getTitle(),
              review.getProblem().getCategory().getLabel(),
              review.getRating(),
              minutesFromSeconds(review.getDurationSeconds()),
              "/system-design/" + review.getProblem().getId(),
              "#ea580c"));
    }

    ZoneId zone = ZoneId.systemDefault();
    Instant startInclusive = weekStart.atStartOfDay(zone).toInstant();
    Instant endExclusive = weekEnd.plusDays(1).atStartOfDay(zone).toInstant();
    for (MockInterview interview :
        mockInterviewRepository.findByStartedAtBetweenOrderByStartedAtDesc(
            startInclusive, endExclusive)) {
      LocalDate date = interview.getStartedAt().atZone(zone).toLocalDate();
      String status = interview.isFinished() ? "Finished" : "In progress";
      entries.add(
          new PracticeEntryView(
              PracticeKind.MOCK_INTERVIEW,
              date,
              interview.getFormat().getLabel(),
              status + " · " + interview.getTotalQuestions() + " questions",
              null,
              minutesFromSeconds(interview.getTotalDurationSeconds()),
              "/mock-interviews/" + interview.getId(),
              "#7c3aed"));
    }

    entries.sort(
        Comparator.comparing(PracticeEntryView::date)
            .reversed()
            .thenComparing(entry -> entry.kind().ordinal())
            .thenComparing(PracticeEntryView::title, String.CASE_INSENSITIVE_ORDER));
    return entries;
  }

  private Map<LocalDate, List<PracticeEntryView>> groupByDay(List<PracticeEntryView> entries) {
    Map<LocalDate, List<PracticeEntryView>> byDay = new HashMap<>();
    for (PracticeEntryView entry : entries) {
      byDay.computeIfAbsent(entry.date(), key -> new ArrayList<>()).add(entry);
    }
    return byDay;
  }

  private List<DayActivityView> buildDays(
      LocalDate weekStart, LocalDate today, Map<LocalDate, List<PracticeEntryView>> byDay) {
    int maxSessions =
        byDay.values().stream().mapToInt(List::size).max().orElse(0);
    List<DayActivityView> days = new ArrayList<>(7);
    for (int i = 0; i < 7; i++) {
      LocalDate date = weekStart.plusDays(i);
      List<PracticeEntryView> dayEntries = byDay.getOrDefault(date, List.of());
      int sessions = dayEntries.size();
      int minutes = dayEntries.stream().mapToInt(PracticeEntryView::durationMinutes).sum();
      int intensity = intensity(sessions, maxSessions);
      days.add(
          new DayActivityView(
              date,
              date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
              date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
              date.equals(today),
              date.isAfter(today),
              sessions,
              minutes,
              intensity,
              dayEntries));
    }
    return days;
  }

  private List<KindBreakdownView> buildBreakdown(List<PracticeEntryView> entries) {
    Map<PracticeKind, Long> sessions = new EnumMap<>(PracticeKind.class);
    Map<PracticeKind, Long> minutes = new EnumMap<>(PracticeKind.class);
    Map<PracticeKind, List<Integer>> ratings = new EnumMap<>(PracticeKind.class);
    for (PracticeKind kind : PracticeKind.values()) {
      sessions.put(kind, 0L);
      minutes.put(kind, 0L);
      ratings.put(kind, new ArrayList<>());
    }
    for (PracticeEntryView entry : entries) {
      sessions.merge(entry.kind(), 1L, Long::sum);
      minutes.merge(entry.kind(), (long) entry.durationMinutes(), Long::sum);
      if (entry.rating() != null) {
        ratings.get(entry.kind()).add(entry.rating());
      }
    }
    long maxSessions = sessions.values().stream().mapToLong(Long::longValue).max().orElse(0);
    List<KindBreakdownView> breakdown = new ArrayList<>();
    for (PracticeKind kind : PracticeKind.values()) {
      long count = sessions.get(kind);
      if (count == 0) {
        continue;
      }
      List<Integer> kindRatings = ratings.get(kind);
      double avg =
          kindRatings.isEmpty()
              ? 0
              : Math.round(
                      kindRatings.stream().mapToInt(Integer::intValue).average().orElse(0) * 10.0)
                  / 10.0;
      int barWidth = maxSessions == 0 ? 0 : (int) Math.round((count * 100.0) / maxSessions);
      breakdown.add(new KindBreakdownView(kind, count, minutes.get(kind), avg, barWidth));
    }
    return breakdown;
  }

  private List<TopicHighlightView> buildTopTopics(List<PracticeEntryView> entries) {
    Map<String, TopicAccumulator> topics = new HashMap<>();
    for (PracticeEntryView entry : entries) {
      if (entry.kind() != PracticeKind.ALGORITHM) {
        continue;
      }
      for (String part : entry.subtitle().split(" · ")) {
        String name = part.trim();
        if (name.isEmpty() || "Algorithms".equalsIgnoreCase(name)) {
          continue;
        }
        TopicAccumulator acc = topics.computeIfAbsent(name, key -> new TopicAccumulator(name, entry.accentColor()));
        acc.sessions++;
      }
    }
    return topics.values().stream()
        .sorted(
            Comparator.comparingLong((TopicAccumulator a) -> a.sessions)
                .reversed()
                .thenComparing(a -> a.name.toLowerCase(Locale.ROOT)))
        .limit(MAX_TOPICS)
        .map(acc -> new TopicHighlightView(acc.name, acc.color, acc.sessions))
        .toList();
  }

  private List<PracticeEntryView> pickHighlights(List<PracticeEntryView> entries) {
    List<PracticeEntryView> rated =
        entries.stream()
            .filter(entry -> entry.rating() != null)
            .sorted(
                Comparator.comparing(PracticeEntryView::rating)
                    .reversed()
                    .thenComparing(PracticeEntryView::durationMinutes)
                    .reversed())
            .toList();
    if (!rated.isEmpty()) {
      return rated.stream().limit(MAX_HIGHLIGHTS).toList();
    }
    return entries.stream().limit(MAX_HIGHLIGHTS).toList();
  }

  private String buildHeadline(long sessions, long activeDays, boolean currentWeek) {
    if (sessions == 0) {
      return currentWeek ? "A quiet week so far" : "No practice logged";
    }
    if (activeDays >= 5) {
      return "Strong, consistent week";
    }
    if (sessions >= 8) {
      return "Busy practice week";
    }
    if (sessions >= 3) {
      return "Solid progress this week";
    }
    return currentWeek ? "You're getting started" : "A light practice week";
  }

  private String buildSummaryLine(
      long sessions, long minutes, double averageRating, List<KindBreakdownView> breakdown) {
    if (sessions == 0) {
      return "Log a review, behavioral answer, system design session, or mock interview to fill this report.";
    }
    String kinds =
        breakdown.stream()
            .map(item -> item.kind().getLabel())
            .reduce((a, b) -> a + ", " + b)
            .orElse("practice");
    String ratingPart =
        averageRating > 0 ? " Avg rating " + averageRating + "/5." : "";
    return sessions
        + (sessions == 1 ? " session" : " sessions")
        + " · "
        + formatDuration(minutes)
        + " · "
        + kinds
        + "."
        + ratingPart;
  }

  private String formatRange(LocalDate start, LocalDate end) {
    if (start.getYear() == end.getYear()) {
      if (start.getMonth() == end.getMonth()) {
        return start.format(DateTimeFormatter.ofPattern("MMM d"))
            + " – "
            + end.format(DateTimeFormatter.ofPattern("d, yyyy"));
      }
      return start.format(RANGE_DAY) + " – " + end.format(RANGE_DAY_YEAR);
    }
    return start.format(RANGE_DAY_YEAR) + " – " + end.format(RANGE_DAY_YEAR);
  }

  private double averageRating(List<PracticeEntryView> entries) {
    List<Integer> ratings =
        entries.stream().map(PracticeEntryView::rating).filter(rating -> rating != null).toList();
    if (ratings.isEmpty()) {
      return 0;
    }
    return Math.round(ratings.stream().mapToInt(Integer::intValue).average().orElse(0) * 10.0)
        / 10.0;
  }

  private int intensity(int sessions, int maxSessions) {
    if (sessions <= 0 || maxSessions <= 0) {
      return 0;
    }
    double ratio = sessions / (double) maxSessions;
    if (ratio >= 0.85) {
      return 4;
    }
    if (ratio >= 0.55) {
      return 3;
    }
    if (ratio >= 0.3) {
      return 2;
    }
    return 1;
  }

  private int minutesFromSeconds(int seconds) {
    if (seconds <= 0) {
      return 0;
    }
    return Math.max(1, (int) Math.round(seconds / 60.0));
  }

  private String formatDuration(long minutes) {
    if (minutes <= 0) {
      return "0 min";
    }
    if (minutes < 60) {
      return minutes + " min";
    }
    long hours = minutes / 60;
    long rem = minutes % 60;
    if (rem == 0) {
      return hours + (hours == 1 ? " hr" : " hrs");
    }
    return hours + "h " + rem + "m";
  }

  private static final class TopicAccumulator {
    private final String name;
    private final String color;
    private long sessions;

    private TopicAccumulator(String name, String color) {
      this.name = name;
      this.color = color;
    }
  }
}
