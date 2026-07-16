package com.projects.knowledge_manager.problem.service;

import com.projects.knowledge_manager.problem.config.ProblemListProperties;
import com.projects.knowledge_manager.problem.dto.BulkProblemForm;
import com.projects.knowledge_manager.problem.dto.BulkProblemRowForm;
import com.projects.knowledge_manager.problem.dto.ProblemDetailView;
import com.projects.knowledge_manager.problem.dto.ProblemFilterCriteria;
import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.dto.ProblemGroupView;
import com.projects.knowledge_manager.problem.dto.ProblemListPageView;
import com.projects.knowledge_manager.problem.dto.ProblemSummaryView;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.mapper.ProblemMapper;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.model.DueStatus;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.problem.repository.ProblemSpecifications;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.tag.dto.TagView;
import com.projects.knowledge_manager.tag.entity.Tag;
import com.projects.knowledge_manager.tag.service.TagService;
import com.projects.knowledge_manager.topic.entity.Topic;
import com.projects.knowledge_manager.topic.repository.TopicRepository;
import com.projects.knowledge_manager.topic.service.TopicNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProblemService {

  private final ProblemRepository problemRepository;
  private final TopicRepository topicRepository;
  private final TagService tagService;
  private final ReviewService reviewService;
  private final ProblemListProperties listProperties;

  public ProblemService(
      ProblemRepository problemRepository,
      TopicRepository topicRepository,
      TagService tagService,
      ReviewService reviewService,
      ProblemListProperties listProperties) {
    this.problemRepository = problemRepository;
    this.topicRepository = topicRepository;
    this.tagService = tagService;
    this.reviewService = reviewService;
    this.listProperties = listProperties;
  }

  public List<ProblemSummaryView> findAll(boolean includeArchived) {
    return findFiltered(ProblemFilterCriteria.defaults(includeArchived));
  }

  public List<ProblemSummaryView> findFiltered(ProblemFilterCriteria criteria) {
    LocalDate today = LocalDate.now();
    LocalDate upcomingLimit = today.plusDays(reviewService.getUpcomingWindowDays());

    return problemRepository
        .findAll(
            ProblemSpecifications.withFilters(
                criteria.archived(),
                criteria.query(),
                criteria.topicId(),
                criteria.difficulty(),
                criteria.tagId(),
                criteria.favorite()),
            Sort.by(Sort.Direction.DESC, "updatedAt"))
        .stream()
        .map(
            problem -> {
              LocalDate nextReviewDate = reviewService.resolveNextReviewDate(problem);
              boolean overdue = nextReviewDate.isBefore(today);
              return ProblemMapper.toSummaryView(problem, nextReviewDate, overdue);
            })
        .filter(summary -> matchesDueStatus(summary, criteria.dueStatus(), today, upcomingLimit))
        .toList();
  }

  public ProblemListPageView findFilteredPage(ProblemFilterCriteria criteria, int page) {
    List<ProblemSummaryView> filtered = findFiltered(criteria);
    int pageSize = Math.max(1, listProperties.getPageSize());
    int totalPages = Math.max(1, (int) Math.ceil((double) filtered.size() / pageSize));
    int safePage = Math.min(Math.max(page, 0), totalPages - 1);
    int fromIndex = safePage * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, filtered.size());
    List<ProblemSummaryView> content =
        fromIndex < toIndex ? filtered.subList(fromIndex, toIndex) : List.of();

    return new ProblemListPageView(content, safePage, totalPages, filtered.size(), pageSize);
  }

  public int getDefaultPageSize() {
    return listProperties.getPageSize();
  }

  public List<ProblemGroupView> findGroupedByTopic(boolean includeArchived) {
    Map<Long, List<ProblemSummaryView>> byTopic =
        findAll(includeArchived).stream()
            .collect(Collectors.groupingBy(ProblemSummaryView::topicId));

    return topicRepository.findAllByOrderByNameAsc().stream()
        .map(
            topic ->
                new ProblemGroupView(
                    topic.getId(),
                    topic.getName(),
                    topic.getColor(),
                    sortByTitle(byTopic.getOrDefault(topic.getId(), List.of()))))
        .toList();
  }

  public List<ProblemGroupView> findGroupedByTag(boolean includeArchived) {
    Map<Long, List<ProblemSummaryView>> byTag = new HashMap<>();
    for (ProblemSummaryView problem : findAll(includeArchived)) {
      for (TagView tag : problem.tags()) {
        byTag.computeIfAbsent(tag.id(), ignored -> new ArrayList<>()).add(problem);
      }
    }

    return tagService.findAll().stream()
        .map(
            tag ->
                new ProblemGroupView(
                    tag.id(), tag.name(), null, sortByTitle(byTag.getOrDefault(tag.id(), List.of()))))
        .toList();
  }

  private List<ProblemSummaryView> sortByTitle(List<ProblemSummaryView> problems) {
    return problems.stream()
        .sorted(Comparator.comparing(ProblemSummaryView::title, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private boolean matchesDueStatus(
      ProblemSummaryView summary, DueStatus dueStatus, LocalDate today, LocalDate upcomingLimit) {
    if (dueStatus == null || dueStatus == DueStatus.ALL) {
      return true;
    }

    LocalDate nextReviewDate = summary.nextReviewDate();

    return switch (dueStatus) {
      case DUE -> nextReviewDate != null && !nextReviewDate.isAfter(today);
      case OVERDUE -> nextReviewDate != null && nextReviewDate.isBefore(today);
      case UPCOMING ->
          nextReviewDate != null
              && !reviewService.isNeverReviewed(summary.id())
              && nextReviewDate.isAfter(today)
              && !nextReviewDate.isAfter(upcomingLimit);
      case NEVER_REVIEWED -> reviewService.isNeverReviewed(summary.id());
      default -> true;
    };
  }

  public ProblemDetailView findDetailById(Long id) {
    return ProblemMapper.toDetailView(getProblemOrThrow(id));
  }

  public ProblemForm findFormById(Long id) {
    return ProblemMapper.toForm(getProblemOrThrow(id));
  }

  @Transactional
  public ProblemDetailView create(ProblemForm form) {
    Topic topic = getTopicOrThrow(form.topicId());
    Set<Tag> tags = tagService.resolveTagsForAssignment(form.tagIds(), form.newTagNames());
    Problem saved = problemRepository.save(ProblemMapper.toEntity(form, topic, tags));
    return ProblemMapper.toDetailView(saved);
  }

  @Transactional
  public int createBulk(BulkProblemForm form) {
    int created = 0;
    if (form.rows() == null) {
      throw new EmptyBulkProblemSubmissionException();
    }

    for (int index = 0; index < form.rows().size(); index++) {
      BulkProblemRowForm row = form.rows().get(index);
      if (row.title() == null || row.title().isBlank()) {
        continue;
      }

      validateBulkRow(row, index);

      Topic topic = getTopicOrThrow(row.topicId());
      Set<Tag> tags = tagService.resolveTagsForAssignment(row.tagIds(), row.newTagNames());

      Difficulty difficulty = row.difficulty() != null ? row.difficulty() : Difficulty.MEDIUM;
      String language = row.language() != null && !row.language().isBlank() ? row.language().trim() : "java";

      ProblemForm problemForm =
          new ProblemForm(
              row.title().trim(),
              normalizeOptional(row.url()),
              difficulty,
              row.description().trim(),
              row.topicId(),
              List.of(),
              "",
              false,
              false,
              language,
              row.sourceCode().trim(),
              "",
              "",
              "");

      problemRepository.save(ProblemMapper.toEntity(problemForm, topic, tags));
      created++;
    }

    if (created == 0) {
      throw new EmptyBulkProblemSubmissionException();
    }
    return created;
  }

  private void validateBulkRow(BulkProblemRowForm row, int index) {
    if (row.topicId() == null) {
      throw new BulkProblemValidationException(
          "rows[" + index + "].topicId", "Topic is required for \"" + row.title().trim() + "\"");
    }
    if (row.description() == null || row.description().isBlank()) {
      throw new BulkProblemValidationException(
          "rows[" + index + "].description",
          "Description is required for \"" + row.title().trim() + "\"");
    }
    if (row.sourceCode() == null || row.sourceCode().isBlank()) {
      throw new BulkProblemValidationException(
          "rows[" + index + "].sourceCode",
          "Solution code is required for \"" + row.title().trim() + "\"");
    }
  }

  @Transactional
  public ProblemDetailView update(Long id, ProblemForm form) {
    Problem problem = getProblemOrThrow(id);
    Topic topic = getTopicOrThrow(form.topicId());
    Set<Tag> tags = tagService.resolveTagsForAssignment(form.tagIds(), form.newTagNames());
    problem.setTopic(topic);
    ProblemMapper.updateEntity(problem, form, tags);
    return ProblemMapper.toDetailView(problem);
  }

  @Transactional
  public void delete(Long id) {
    problemRepository.delete(getProblemOrThrow(id));
  }

  @Transactional
  public void toggleFavorite(Long id) {
    Problem problem = getProblemOrThrow(id);
    problem.setFavorite(!problem.isFavorite());
  }

  @Transactional
  public void toggleArchive(Long id) {
    Problem problem = getProblemOrThrow(id);
    problem.setArchived(!problem.isArchived());
  }

  private Problem getProblemOrThrow(Long id) {
    return problemRepository
        .findDetailedById(id)
        .orElseThrow(() -> new ProblemNotFoundException(id));
  }

  private Topic getTopicOrThrow(Long topicId) {
    return topicRepository
        .findById(topicId)
        .orElseThrow(() -> new TopicNotFoundException(topicId));
  }

  private String normalizeOptional(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
