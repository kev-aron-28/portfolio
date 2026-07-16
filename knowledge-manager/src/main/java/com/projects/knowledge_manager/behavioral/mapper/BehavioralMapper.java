package com.projects.knowledge_manager.behavioral.mapper;

import com.projects.knowledge_manager.behavioral.dto.BehavioralPracticeView;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionForm;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionSummaryView;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionView;
import com.projects.knowledge_manager.behavioral.entity.BehavioralPractice;
import com.projects.knowledge_manager.behavioral.entity.BehavioralQuestion;
import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;
import java.time.LocalDate;

public final class BehavioralMapper {

  private BehavioralMapper() {}

  public static BehavioralQuestion toEntity(BehavioralQuestionForm form) {
    BehavioralQuestion question =
        new BehavioralQuestion(form.title().trim(), form.category(), form.question().trim());
    applyAnswerFields(question, form);
    return question;
  }

  public static void updateEntity(BehavioralQuestion question, BehavioralQuestionForm form) {
    question.setTitle(form.title().trim());
    question.setCategory(form.category());
    question.setQuestion(form.question().trim());
    applyAnswerFields(question, form);
  }

  public static BehavioralQuestionForm toForm(BehavioralQuestion question) {
    return new BehavioralQuestionForm(
        question.getTitle(),
        question.getCategory(),
        question.getQuestion(),
        nullToEmpty(question.getAnswerSituation()),
        nullToEmpty(question.getAnswerTask()),
        nullToEmpty(question.getAnswerAction()),
        nullToEmpty(question.getAnswerResult()),
        nullToEmpty(question.getNotes()));
  }

  public static BehavioralQuestionView toView(
      BehavioralQuestion question,
      LocalDate nextReviewDate,
      LocalDate today,
      boolean neverPracticed) {
    return new BehavioralQuestionView(
        question.getId(),
        question.getTitle(),
        question.getCategory(),
        question.getQuestion(),
        question.getAnswerSituation(),
        question.getAnswerTask(),
        question.getAnswerAction(),
        question.getAnswerResult(),
        question.getNotes(),
        nextReviewDate,
        !nextReviewDate.isAfter(today),
        nextReviewDate.isBefore(today),
        neverPracticed,
        question.getCreatedAt(),
        question.getUpdatedAt());
  }

  public static BehavioralQuestionSummaryView toSummary(
      BehavioralQuestion question,
      LocalDate nextReviewDate,
      LocalDate today,
      boolean neverPracticed,
      Integer lastRating) {
    return new BehavioralQuestionSummaryView(
        question.getId(),
        question.getTitle(),
        question.getCategory(),
        nextReviewDate,
        !nextReviewDate.isAfter(today),
        nextReviewDate.isBefore(today),
        neverPracticed,
        lastRating);
  }

  public static BehavioralPracticeView toPracticeView(BehavioralPractice practice) {
    return new BehavioralPracticeView(
        practice.getId(),
        practice.getQuestion().getId(),
        practice.getQuestion().getTitle(),
        practice.getPracticeDate(),
        practice.getDurationSeconds(),
        practice.getRating(),
        practice.getNextReviewDate());
  }

  public static ReviewHistoryEntry toHistoryEntry(BehavioralPractice practice) {
    return new ReviewHistoryEntry(
        practice.getPracticeDate(), practice.getRating(), practice.getNextReviewDate());
  }

  private static void applyAnswerFields(BehavioralQuestion question, BehavioralQuestionForm form) {
    question.setAnswerSituation(normalize(form.answerSituation()));
    question.setAnswerTask(normalize(form.answerTask()));
    question.setAnswerAction(normalize(form.answerAction()));
    question.setAnswerResult(normalize(form.answerResult()));
    question.setNotes(normalize(form.notes()));
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
