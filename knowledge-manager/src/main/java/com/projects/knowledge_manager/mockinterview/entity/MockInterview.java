package com.projects.knowledge_manager.mockinterview.entity;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mock_interviews")
public class MockInterview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private InterviewFormat format;

  @Column(name = "started_at", nullable = false)
  private Instant startedAt;

  @Column(name = "finished_at")
  private Instant finishedAt;

  @Column(name = "total_duration_seconds", nullable = false)
  private int totalDurationSeconds;

  @Column(name = "total_questions", nullable = false)
  private int totalQuestions;

  @Column(name = "profile_id")
  private Long profileId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("itemOrder ASC")
  private List<MockInterviewItem> items = new ArrayList<>();

  protected MockInterview() {}

  public MockInterview(InterviewFormat format, Instant startedAt) {
    this.format = format;
    this.startedAt = startedAt;
  }

  public MockInterview(InterviewFormat format, Instant startedAt, Long profileId) {
    this.format = format;
    this.startedAt = startedAt;
    this.profileId = profileId;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public void addItem(MockInterviewItem item) {
    items.add(item);
    item.setInterview(this);
    totalQuestions = items.size();
  }

  public Long getId() {
    return id;
  }

  public InterviewFormat getFormat() {
    return format;
  }

  public Long getProfileId() {
    return profileId;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public Instant getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(Instant finishedAt) {
    this.finishedAt = finishedAt;
  }

  public int getTotalDurationSeconds() {
    return totalDurationSeconds;
  }

  public void setTotalDurationSeconds(int totalDurationSeconds) {
    this.totalDurationSeconds = totalDurationSeconds;
  }

  public int getTotalQuestions() {
    return totalQuestions;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public List<MockInterviewItem> getItems() {
    return items;
  }

  public boolean isFinished() {
    return finishedAt != null;
  }
}
