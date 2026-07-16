package com.projects.knowledge_manager.behavioral.service;

import com.projects.knowledge_manager.behavioral.dto.BehavioralPracticeForm;
import com.projects.knowledge_manager.behavioral.dto.BehavioralPracticeView;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionForm;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionSummaryView;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionView;
import com.projects.knowledge_manager.behavioral.dto.BehavioralStatsView;
import com.projects.knowledge_manager.behavioral.entity.BehavioralPractice;
import com.projects.knowledge_manager.behavioral.entity.BehavioralQuestion;
import com.projects.knowledge_manager.behavioral.mapper.BehavioralMapper;
import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import com.projects.knowledge_manager.behavioral.repository.BehavioralPracticeRepository;
import com.projects.knowledge_manager.behavioral.repository.BehavioralQuestionRepository;
import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;
import com.projects.knowledge_manager.review.scheduler.ReviewResult;
import com.projects.knowledge_manager.review.scheduler.ReviewScheduler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BehavioralQuestionService {

  private final BehavioralQuestionRepository questionRepository;
  private final BehavioralPracticeRepository practiceRepository;
  private final ReviewScheduler reviewScheduler;
  private final ReviewSchedulingProperties schedulingProperties;

  public BehavioralQuestionService(
      BehavioralQuestionRepository questionRepository,
      BehavioralPracticeRepository practiceRepository,
      ReviewScheduler reviewScheduler,
      ReviewSchedulingProperties schedulingProperties) {
    this.questionRepository = questionRepository;
    this.practiceRepository = practiceRepository;
    this.reviewScheduler = reviewScheduler;
    this.schedulingProperties = schedulingProperties;
  }

  public List<BehavioralQuestionSummaryView> findFiltered(BehavioralCategory category, String query) {
    LocalDate today = LocalDate.now();
    String normalizedQuery = query == null ? "" : query.trim();
    List<BehavioralQuestion> questions;

    if (category != null && !normalizedQuery.isEmpty()) {
      questions = questionRepository.searchByCategoryAndText(category, normalizedQuery);
    } else if (category != null) {
      questions = questionRepository.findByCategoryOrderByTitleAsc(category);
    } else if (!normalizedQuery.isEmpty()) {
      questions = questionRepository.searchByTitleOrQuestion(normalizedQuery);
    } else {
      questions = questionRepository.findAllByOrderByTitleAsc();
    }

    return questions.stream().map(question -> toSummary(question, today)).toList();
  }

  public BehavioralQuestionView findById(Long id) {
    BehavioralQuestion question = getQuestionOrThrow(id);
    LocalDate today = LocalDate.now();
    boolean neverPracticed = isNeverPracticed(id);
    return BehavioralMapper.toView(question, resolveNextReviewDate(id), today, neverPracticed);
  }

  public BehavioralQuestionForm findFormById(Long id) {
    return BehavioralMapper.toForm(getQuestionOrThrow(id));
  }

  public BehavioralStatsView buildStats() {
    LocalDate today = LocalDate.now();
    List<BehavioralQuestion> questions = questionRepository.findAll();
    long dueToday =
        questions.stream()
            .filter(question -> !resolveNextReviewDate(question.getId()).isAfter(today))
            .count();
    Double average = practiceRepository.averageDurationSeconds();
    return new BehavioralStatsView(
        questions.size(),
        dueToday,
        practiceRepository.count(),
        average == null ? 0 : Math.round(average * 10.0) / 10.0,
        practiceRepository.findLastPracticeDate().orElse(null));
  }

  public Optional<BehavioralQuestionView> findRandomDueQuestion() {
    LocalDate today = LocalDate.now();
    List<BehavioralQuestion> due = new ArrayList<>();
    for (BehavioralQuestion question : questionRepository.findAll()) {
      if (!resolveNextReviewDate(question.getId()).isAfter(today)) {
        due.add(question);
      }
    }
    if (due.isEmpty()) {
      return Optional.empty();
    }
    Collections.shuffle(due);
    BehavioralQuestion selected = due.getFirst();
    return Optional.of(
        BehavioralMapper.toView(
            selected,
            resolveNextReviewDate(selected.getId()),
            today,
            isNeverPracticed(selected.getId())));
  }

  public List<BehavioralPracticeView> findPracticeHistory(Long questionId) {
    ensureQuestionExists(questionId);
    return practiceRepository.findByQuestionIdOrderByPracticeDateDescIdDesc(questionId).stream()
        .map(BehavioralMapper::toPracticeView)
        .toList();
  }

  @Transactional
  public BehavioralQuestionView create(BehavioralQuestionForm form) {
    BehavioralQuestion saved = questionRepository.save(BehavioralMapper.toEntity(form));
    return BehavioralMapper.toView(saved, LocalDate.now(), LocalDate.now(), true);
  }

  @Transactional
  public BehavioralQuestionView update(Long id, BehavioralQuestionForm form) {
    BehavioralQuestion question = getQuestionOrThrow(id);
    BehavioralMapper.updateEntity(question, form);
    boolean neverPracticed = isNeverPracticed(id);
    LocalDate today = LocalDate.now();
    return BehavioralMapper.toView(question, resolveNextReviewDate(id), today, neverPracticed);
  }

  @Transactional
  public void delete(Long id) {
    BehavioralQuestion question = getQuestionOrThrow(id);
    questionRepository.delete(question);
  }

  @Transactional
  public BehavioralPracticeView recordPractice(Long questionId, BehavioralPracticeForm form) {
    BehavioralQuestion question = getQuestionOrThrow(questionId);
    validateRating(form.rating());

    List<ReviewHistoryEntry> history =
        practiceRepository.findByQuestionIdOrderByPracticeDateDescIdDesc(questionId).stream()
            .map(BehavioralMapper::toHistoryEntry)
            .toList();

    ReviewResult result = new ReviewResult(form.practiceDate(), form.rating());
    LocalDate nextReviewDate = reviewScheduler.calculateNextReview(result, history);

    BehavioralPractice practice =
        new BehavioralPractice(
            question,
            form.practiceDate(),
            form.durationSeconds(),
            form.rating(),
            nextReviewDate);

    return BehavioralMapper.toPracticeView(practiceRepository.save(practice));
  }

  public LocalDate resolveNextReviewDate(Long questionId) {
    return findLatestPractice(questionId)
        .map(BehavioralPractice::getNextReviewDate)
        .orElseGet(
            () -> {
              BehavioralQuestion question = getQuestionOrThrow(questionId);
              return question
                  .getCreatedAt()
                  .atZone(schedulingProperties.getZoneId())
                  .toLocalDate();
            });
  }

  public boolean isNeverPracticed(Long questionId) {
    return findLatestPractice(questionId).isEmpty();
  }

  private BehavioralQuestionSummaryView toSummary(BehavioralQuestion question, LocalDate today) {
    Long id = question.getId();
    Optional<BehavioralPractice> latest = findLatestPractice(id);
    return BehavioralMapper.toSummary(
        question,
        resolveNextReviewDate(id),
        today,
        latest.isEmpty(),
        latest.map(BehavioralPractice::getRating).orElse(null));
  }

  private Optional<BehavioralPractice> findLatestPractice(Long questionId) {
    return practiceRepository.findByQuestionIdOrderByPracticeDateDescIdDesc(questionId).stream()
        .findFirst();
  }

  private BehavioralQuestion getQuestionOrThrow(Long id) {
    return questionRepository
        .findById(id)
        .orElseThrow(() -> new BehavioralQuestionNotFoundException(id));
  }

  private void ensureQuestionExists(Long id) {
    if (!questionRepository.existsById(id)) {
      throw new BehavioralQuestionNotFoundException(id);
    }
  }

  private void validateRating(Integer rating) {
    if (PracticeRating.fromQuality(rating) == null) {
      throw new IllegalArgumentException(
          "Rating must be one of Forgot(1), Hard(3), Good(4), or Easy(5).");
    }
  }
}
