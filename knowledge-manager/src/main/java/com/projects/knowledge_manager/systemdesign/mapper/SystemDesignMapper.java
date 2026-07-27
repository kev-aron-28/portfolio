package com.projects.knowledge_manager.systemdesign.mapper;

import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemForm;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemSummaryView;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemView;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignReviewView;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignDocument;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignProblem;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignReview;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class SystemDesignMapper {

  private SystemDesignMapper() {}

  public static SystemDesignProblem toEntity(SystemDesignProblemForm form) {
    SystemDesignProblem problem =
        new SystemDesignProblem(
            form.title().trim(), form.category(), form.difficulty(), form.description().trim());
    applyMetadata(problem, form);
    SystemDesignDocument document = new SystemDesignDocument(problem);
    applyDocumentFields(document, form);
    problem.attachDocument(document);
    return problem;
  }

  public static void updateEntity(SystemDesignProblem problem, SystemDesignProblemForm form) {
    problem.setTitle(form.title().trim());
    problem.setCategory(form.category());
    problem.setDifficulty(form.difficulty());
    problem.setDescription(form.description().trim());
    applyMetadata(problem, form);
    SystemDesignDocument document = problem.getDocument();
    if (document == null) {
      document = new SystemDesignDocument(problem);
      problem.attachDocument(document);
    }
    applyDocumentFields(document, form);
  }

  public static SystemDesignProblemForm toForm(SystemDesignProblem problem) {
    SystemDesignDocument document = problem.getDocument();
    return new SystemDesignProblemForm(
        problem.getTitle(),
        problem.getCategory(),
        problem.getDifficulty(),
        problem.getDescription(),
        nullToEmpty(problem.getOriginalSource()),
        problem.getEstimatedInterviewTime(),
        problem.isFavorite(),
        String.join(", ", sortedTags(problem.getTags())),
        nullToEmpty(document == null ? null : document.getOverview()),
        nullToEmpty(document == null ? null : document.getFunctionalRequirements()),
        nullToEmpty(document == null ? null : document.getNonFunctionalRequirements()),
        nullToEmpty(document == null ? null : document.getAssumptions()),
        nullToEmpty(document == null ? null : document.getHighLevelArchitecture()),
        nullToEmpty(document == null ? null : document.getComponents()),
        nullToEmpty(document == null ? null : document.getDatabaseDesign()),
        nullToEmpty(document == null ? null : document.getApiDesign()),
        nullToEmpty(document == null ? null : document.getScalingStrategy()),
        nullToEmpty(document == null ? null : document.getCachingStrategy()),
        nullToEmpty(document == null ? null : document.getLoadBalancing()),
        nullToEmpty(document == null ? null : document.getMessaging()),
        nullToEmpty(document == null ? null : document.getTradeoffs()),
        nullToEmpty(document == null ? null : document.getBottlenecks()),
        nullToEmpty(document == null ? null : document.getLessonsLearned()),
        nullToEmpty(document == null ? null : document.getPersonalNotes()));
  }

  public static SystemDesignProblemView toView(
      SystemDesignProblem problem,
      LocalDate nextReviewDate,
      LocalDate today,
      boolean neverReviewed) {
    SystemDesignDocument document = problem.getDocument();
    return new SystemDesignProblemView(
        problem.getId(),
        problem.getTitle(),
        problem.getCategory(),
        problem.getDifficulty(),
        problem.getDescription(),
        problem.getOriginalSource(),
        problem.getEstimatedInterviewTime(),
        problem.isFavorite(),
        sortedTags(problem.getTags()),
        problem.getWhiteboardJson(),
        document == null ? null : document.getOverview(),
        document == null ? null : document.getFunctionalRequirements(),
        document == null ? null : document.getNonFunctionalRequirements(),
        document == null ? null : document.getAssumptions(),
        document == null ? null : document.getHighLevelArchitecture(),
        document == null ? null : document.getComponents(),
        document == null ? null : document.getDatabaseDesign(),
        document == null ? null : document.getApiDesign(),
        document == null ? null : document.getScalingStrategy(),
        document == null ? null : document.getCachingStrategy(),
        document == null ? null : document.getLoadBalancing(),
        document == null ? null : document.getMessaging(),
        document == null ? null : document.getTradeoffs(),
        document == null ? null : document.getBottlenecks(),
        document == null ? null : document.getLessonsLearned(),
        document == null ? null : document.getPersonalNotes(),
        nextReviewDate,
        !nextReviewDate.isAfter(today),
        nextReviewDate.isBefore(today),
        neverReviewed,
        problem.getCreatedAt(),
        problem.getUpdatedAt());
  }

  public static SystemDesignProblemSummaryView toSummary(
      SystemDesignProblem problem,
      LocalDate nextReviewDate,
      LocalDate today,
      boolean neverReviewed,
      Integer lastRating,
      long reviewCount) {
    return new SystemDesignProblemSummaryView(
        problem.getId(),
        problem.getTitle(),
        problem.getCategory(),
        problem.getDifficulty(),
        problem.isFavorite(),
        sortedTags(problem.getTags()),
        nextReviewDate,
        !nextReviewDate.isAfter(today),
        nextReviewDate.isBefore(today),
        neverReviewed,
        lastRating,
        reviewCount);
  }

  public static SystemDesignReviewView toReviewView(SystemDesignReview review) {
    return new SystemDesignReviewView(
        review.getId(),
        review.getProblem().getId(),
        review.getProblem().getTitle(),
        review.getReviewDate(),
        review.getDurationSeconds(),
        review.getRating(),
        review.getNextReviewDate());
  }

  public static ReviewHistoryEntry toHistoryEntry(SystemDesignReview review) {
    return new ReviewHistoryEntry(
        review.getReviewDate(), review.getRating(), review.getNextReviewDate());
  }

  public static Set<String> parseTags(String raw) {
    if (raw == null || raw.isBlank()) {
      return Set.of();
    }
    return Arrays.stream(raw.split(","))
        .map(String::trim)
        .filter(tag -> !tag.isEmpty())
        .map(tag -> tag.length() > 100 ? tag.substring(0, 100) : tag)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static void applyMetadata(SystemDesignProblem problem, SystemDesignProblemForm form) {
    problem.setOriginalSource(normalize(form.originalSource()));
    problem.setEstimatedInterviewTime(form.estimatedInterviewTime());
    problem.setFavorite(form.favorite());
    problem.setTags(parseTags(form.tags()));
  }

  private static void applyDocumentFields(
      SystemDesignDocument document, SystemDesignProblemForm form) {
    document.setOverview(normalize(form.overview()));
    document.setFunctionalRequirements(normalize(form.functionalRequirements()));
    document.setNonFunctionalRequirements(normalize(form.nonFunctionalRequirements()));
    document.setAssumptions(normalize(form.assumptions()));
    document.setHighLevelArchitecture(normalize(form.highLevelArchitecture()));
    document.setComponents(normalize(form.components()));
    document.setDatabaseDesign(normalize(form.databaseDesign()));
    document.setApiDesign(normalize(form.apiDesign()));
    document.setScalingStrategy(normalize(form.scalingStrategy()));
    document.setCachingStrategy(normalize(form.cachingStrategy()));
    document.setLoadBalancing(normalize(form.loadBalancing()));
    document.setMessaging(normalize(form.messaging()));
    document.setTradeoffs(normalize(form.tradeoffs()));
    document.setBottlenecks(normalize(form.bottlenecks()));
    document.setLessonsLearned(normalize(form.lessonsLearned()));
    document.setPersonalNotes(normalize(form.personalNotes()));
  }

  private static List<String> sortedTags(Set<String> tags) {
    return tags.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
  }

  private static String normalize(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}
