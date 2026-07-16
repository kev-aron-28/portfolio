package com.projects.knowledge_manager.behavioral.entity;

import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "behavioral_questions")
public class BehavioralQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private BehavioralCategory category;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String question;

  @Column(name = "answer_situation", columnDefinition = "TEXT")
  private String answerSituation;

  @Column(name = "answer_task", columnDefinition = "TEXT")
  private String answerTask;

  @Column(name = "answer_action", columnDefinition = "TEXT")
  private String answerAction;

  @Column(name = "answer_result", columnDefinition = "TEXT")
  private String answerResult;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("practiceDate DESC, id DESC")
  private List<BehavioralPractice> practices = new ArrayList<>();

  protected BehavioralQuestion() {}

  public BehavioralQuestion(String title, BehavioralCategory category, String question) {
    this.title = title;
    this.category = category;
    this.question = question;
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

  public BehavioralCategory getCategory() {
    return category;
  }

  public void setCategory(BehavioralCategory category) {
    this.category = category;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswerSituation() {
    return answerSituation;
  }

  public void setAnswerSituation(String answerSituation) {
    this.answerSituation = answerSituation;
  }

  public String getAnswerTask() {
    return answerTask;
  }

  public void setAnswerTask(String answerTask) {
    this.answerTask = answerTask;
  }

  public String getAnswerAction() {
    return answerAction;
  }

  public void setAnswerAction(String answerAction) {
    this.answerAction = answerAction;
  }

  public String getAnswerResult() {
    return answerResult;
  }

  public void setAnswerResult(String answerResult) {
    this.answerResult = answerResult;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public List<BehavioralPractice> getPractices() {
    return practices;
  }
}
