package com.projects.knowledge_manager.systemdesign.entity;

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
@Table(name = "system_design_reviews")
public class SystemDesignReview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "problem_id", nullable = false)
  private SystemDesignProblem problem;

  @Column(name = "review_date", nullable = false)
  private LocalDate reviewDate;

  @Column(name = "duration_seconds", nullable = false)
  private int durationSeconds;

  @Column(nullable = false)
  private int rating;

  @Column(name = "next_review_date", nullable = false)
  private LocalDate nextReviewDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected SystemDesignReview() {}

  public SystemDesignReview(
      SystemDesignProblem problem,
      LocalDate reviewDate,
      int durationSeconds,
      int rating,
      LocalDate nextReviewDate) {
    this.problem = problem;
    this.reviewDate = reviewDate;
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

  public SystemDesignProblem getProblem() {
    return problem;
  }

  public void setProblem(SystemDesignProblem problem) {
    this.problem = problem;
  }

  public LocalDate getReviewDate() {
    return reviewDate;
  }

  public int getDurationSeconds() {
    return durationSeconds;
  }

  public int getRating() {
    return rating;
  }

  public LocalDate getNextReviewDate() {
    return nextReviewDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
