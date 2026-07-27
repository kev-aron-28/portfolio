package com.projects.knowledge_manager.mockinterview.entity;

import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "mock_interview_items")
public class MockInterviewItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "interview_id", nullable = false)
  private MockInterview interview;

  @Column(name = "item_order", nullable = false)
  private int itemOrder;

  @Enumerated(EnumType.STRING)
  @Column(name = "question_type", nullable = false, length = 20)
  private QuestionType questionType;

  @Column(name = "problem_id")
  private Long problemId;

  @Column(name = "behavioral_question_id")
  private Long behavioralQuestionId;

  @Column(name = "system_design_problem_id")
  private Long systemDesignProblemId;

  @Column(nullable = false, length = 300)
  private String title;

  @Column(name = "duration_seconds")
  private Integer durationSeconds;

  @Column
  private Integer rating;

  @Column(name = "completed_at")
  private Instant completedAt;

  protected MockInterviewItem() {}

  public MockInterviewItem(
      int itemOrder,
      QuestionType questionType,
      Long problemId,
      Long behavioralQuestionId,
      Long systemDesignProblemId,
      String title) {
    this.itemOrder = itemOrder;
    this.questionType = questionType;
    this.problemId = problemId;
    this.behavioralQuestionId = behavioralQuestionId;
    this.systemDesignProblemId = systemDesignProblemId;
    this.title = title;
  }

  public void complete(int durationSeconds, int rating, Instant completedAt) {
    this.durationSeconds = durationSeconds;
    this.rating = rating;
    this.completedAt = completedAt;
  }

  public Long getId() {
    return id;
  }

  public MockInterview getInterview() {
    return interview;
  }

  public void setInterview(MockInterview interview) {
    this.interview = interview;
  }

  public int getItemOrder() {
    return itemOrder;
  }

  public QuestionType getQuestionType() {
    return questionType;
  }

  public Long getProblemId() {
    return problemId;
  }

  public Long getBehavioralQuestionId() {
    return behavioralQuestionId;
  }

  public Long getSystemDesignProblemId() {
    return systemDesignProblemId;
  }

  public String getTitle() {
    return title;
  }

  public Integer getDurationSeconds() {
    return durationSeconds;
  }

  public Integer getRating() {
    return rating;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public boolean isCompleted() {
    return completedAt != null;
  }
}
