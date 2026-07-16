package com.projects.knowledge_manager.behavioral.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "behavioral_practices")
public class BehavioralPractice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "question_id", nullable = false)
  private BehavioralQuestion question;

  @Column(name = "practice_date", nullable = false)
  private LocalDate practiceDate;

  @Column(name = "duration_seconds", nullable = false)
  private int durationSeconds;

  @Column(nullable = false)
  private int rating;

  @Column(name = "next_review_date", nullable = false)
  private LocalDate nextReviewDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected BehavioralPractice() {}

  public BehavioralPractice(
      BehavioralQuestion question,
      LocalDate practiceDate,
      int durationSeconds,
      int rating,
      LocalDate nextReviewDate) {
    this.question = question;
    this.practiceDate = practiceDate;
    this.durationSeconds = durationSeconds;
    this.rating = rating;
    this.nextReviewDate = nextReviewDate;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public BehavioralQuestion getQuestion() {
    return question;
  }

  public void setQuestion(BehavioralQuestion question) {
    this.question = question;
  }

  public LocalDate getPracticeDate() {
    return practiceDate;
  }

  public void setPracticeDate(LocalDate practiceDate) {
    this.practiceDate = practiceDate;
  }

  public int getDurationSeconds() {
    return durationSeconds;
  }

  public void setDurationSeconds(int durationSeconds) {
    this.durationSeconds = durationSeconds;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public LocalDate getNextReviewDate() {
    return nextReviewDate;
  }

  public void setNextReviewDate(LocalDate nextReviewDate) {
    this.nextReviewDate = nextReviewDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
