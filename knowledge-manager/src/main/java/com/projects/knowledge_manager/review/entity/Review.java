package com.projects.knowledge_manager.review.entity;

import com.projects.knowledge_manager.problem.entity.Problem;
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
@Table(name = "reviews")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;

  @Column(name = "review_date", nullable = false)
  private LocalDate reviewDate;

  @Column(nullable = false)
  private int rating;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(name = "next_review_date", nullable = false)
  private LocalDate nextReviewDate;

  @Column(name = "review_duration", nullable = false)
  private int reviewDuration;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected Review() {}

  public Review(Problem problem, LocalDate reviewDate, int rating, LocalDate nextReviewDate, int reviewDuration) {
    this.problem = problem;
    this.reviewDate = reviewDate;
    this.rating = rating;
    this.nextReviewDate = nextReviewDate;
    this.reviewDuration = reviewDuration;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Problem getProblem() {
    return problem;
  }

  public void setProblem(Problem problem) {
    this.problem = problem;
  }

  public LocalDate getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(LocalDate reviewDate) {
    this.reviewDate = reviewDate;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public LocalDate getNextReviewDate() {
    return nextReviewDate;
  }

  public void setNextReviewDate(LocalDate nextReviewDate) {
    this.nextReviewDate = nextReviewDate;
  }

  public int getReviewDuration() {
    return reviewDuration;
  }

  public void setReviewDuration(int reviewDuration) {
    this.reviewDuration = reviewDuration;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
