package com.projects.knowledge_manager.systemdesign.entity;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "system_design_problems")
public class SystemDesignProblem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private SystemDesignCategory category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Difficulty difficulty;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "original_source", length = 500)
  private String originalSource;

  @Column(name = "estimated_interview_time")
  private Integer estimatedInterviewTime;

  @Column(nullable = false)
  private boolean favorite;

  @Column(name = "whiteboard_json", columnDefinition = "TEXT")
  private String whiteboardJson;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToOne(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private SystemDesignDocument document;

  @ElementCollection
  @CollectionTable(name = "system_design_tags", joinColumns = @JoinColumn(name = "problem_id"))
  @Column(name = "tag", nullable = false, length = 100)
  private Set<String> tags = new HashSet<>();

  @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("reviewDate DESC, id DESC")
  private List<SystemDesignReview> reviews = new ArrayList<>();

  protected SystemDesignProblem() {}

  public SystemDesignProblem(
      String title,
      SystemDesignCategory category,
      Difficulty difficulty,
      String description) {
    this.title = title;
    this.category = category;
    this.difficulty = difficulty;
    this.description = description;
  }

  @PrePersist
  void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public void attachDocument(SystemDesignDocument document) {
    this.document = document;
    document.setProblem(this);
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public SystemDesignCategory getCategory() {
    return category;
  }

  public void setCategory(SystemDesignCategory category) {
    this.category = category;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOriginalSource() {
    return originalSource;
  }

  public void setOriginalSource(String originalSource) {
    this.originalSource = originalSource;
  }

  public Integer getEstimatedInterviewTime() {
    return estimatedInterviewTime;
  }

  public void setEstimatedInterviewTime(Integer estimatedInterviewTime) {
    this.estimatedInterviewTime = estimatedInterviewTime;
  }

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  public String getWhiteboardJson() {
    return whiteboardJson;
  }

  public void setWhiteboardJson(String whiteboardJson) {
    this.whiteboardJson = whiteboardJson;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public SystemDesignDocument getDocument() {
    return document;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags == null ? new HashSet<>() : new HashSet<>(tags);
  }

  public List<SystemDesignReview> getReviews() {
    return reviews;
  }
}
