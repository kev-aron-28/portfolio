package com.projects.knowledge_manager.problem.entity;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.tag.entity.Tag;
import com.projects.knowledge_manager.topic.entity.Topic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "problems")
public class Problem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(length = 500)
  private String url;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Difficulty difficulty;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private boolean favorite;

  @Column(nullable = false)
  private boolean archived;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "topic_id", nullable = false)
  private Topic topic;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "problem_tags",
      joinColumns = @JoinColumn(name = "problem_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

  @OneToOne(mappedBy = "problem", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  private Solution solution;

  @OneToMany(mappedBy = "problem", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  private List<com.projects.knowledge_manager.review.entity.Review> reviews = new ArrayList<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected Problem() {}

  public Problem(String title, Difficulty difficulty, Topic topic) {
    this.title = title;
    this.difficulty = difficulty;
    this.topic = topic;
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

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  public Solution getSolution() {
    return solution;
  }

  public void setSolution(Solution solution) {
    this.solution = solution;
    if (solution != null) {
      solution.setProblem(this);
    }
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public List<com.projects.knowledge_manager.review.entity.Review> getReviews() {
    return reviews;
  }
}
