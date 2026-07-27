package com.projects.knowledge_manager.interviewprofile.entity;

import com.projects.knowledge_manager.behavioral.entity.BehavioralQuestion;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignProblem;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "interview_profiles")
public class InterviewProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100, unique = true)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(length = 100)
  private String company;

  @Column(nullable = false, length = 7)
  private String color;

  @Column(nullable = false, length = 50)
  private String icon;

  @Column(name = "behavioral_question_count", nullable = false)
  private int behavioralQuestionCount;

  @Column(name = "algorithm_question_count", nullable = false)
  private int algorithmQuestionCount;

  @Column(name = "system_design_question_count", nullable = false)
  private int systemDesignQuestionCount;

  @Column(name = "max_duration_minutes")
  private Integer maxDurationMinutes;

  @Column(nullable = false)
  private boolean archived;

  /** Extensible JSON bag for future profile options without schema churn. */
  @Column(name = "extra_settings_json", columnDefinition = "TEXT")
  private String extraSettingsJson;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "interview_profile_target_difficulties",
      joinColumns = @JoinColumn(name = "profile_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "difficulty", nullable = false, length = 20)
  private Set<Difficulty> targetDifficulties = EnumSet.noneOf(Difficulty.class);

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "interview_profile_problems",
      joinColumns = @JoinColumn(name = "profile_id"),
      inverseJoinColumns = @JoinColumn(name = "problem_id"))
  private Set<Problem> problems = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "interview_profile_behavioral_questions",
      joinColumns = @JoinColumn(name = "profile_id"),
      inverseJoinColumns = @JoinColumn(name = "behavioral_question_id"))
  private Set<BehavioralQuestion> behavioralQuestions = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "interview_profile_system_design_problems",
      joinColumns = @JoinColumn(name = "profile_id"),
      inverseJoinColumns = @JoinColumn(name = "system_design_problem_id"))
  private Set<SystemDesignProblem> systemDesignProblems = new HashSet<>();

  protected InterviewProfile() {}

  public InterviewProfile(String name, String color, String icon) {
    this.name = name;
    this.color = color;
    this.icon = icon;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public int getBehavioralQuestionCount() {
    return behavioralQuestionCount;
  }

  public void setBehavioralQuestionCount(int behavioralQuestionCount) {
    this.behavioralQuestionCount = behavioralQuestionCount;
  }

  public int getAlgorithmQuestionCount() {
    return algorithmQuestionCount;
  }

  public void setAlgorithmQuestionCount(int algorithmQuestionCount) {
    this.algorithmQuestionCount = algorithmQuestionCount;
  }

  public int getSystemDesignQuestionCount() {
    return systemDesignQuestionCount;
  }

  public void setSystemDesignQuestionCount(int systemDesignQuestionCount) {
    this.systemDesignQuestionCount = systemDesignQuestionCount;
  }

  public Integer getMaxDurationMinutes() {
    return maxDurationMinutes;
  }

  public void setMaxDurationMinutes(Integer maxDurationMinutes) {
    this.maxDurationMinutes = maxDurationMinutes;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public String getExtraSettingsJson() {
    return extraSettingsJson;
  }

  public void setExtraSettingsJson(String extraSettingsJson) {
    this.extraSettingsJson = extraSettingsJson;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Set<Difficulty> getTargetDifficulties() {
    return targetDifficulties;
  }

  public void setTargetDifficulties(Set<Difficulty> targetDifficulties) {
    this.targetDifficulties =
        targetDifficulties == null || targetDifficulties.isEmpty()
            ? EnumSet.noneOf(Difficulty.class)
            : EnumSet.copyOf(targetDifficulties);
  }

  public Set<Problem> getProblems() {
    return problems;
  }

  public void setProblems(Set<Problem> problems) {
    this.problems = problems == null ? new HashSet<>() : new HashSet<>(problems);
  }

  public Set<BehavioralQuestion> getBehavioralQuestions() {
    return behavioralQuestions;
  }

  public void setBehavioralQuestions(Set<BehavioralQuestion> behavioralQuestions) {
    this.behavioralQuestions =
        behavioralQuestions == null ? new HashSet<>() : new HashSet<>(behavioralQuestions);
  }

  public Set<SystemDesignProblem> getSystemDesignProblems() {
    return systemDesignProblems;
  }

  public void setSystemDesignProblems(Set<SystemDesignProblem> systemDesignProblems) {
    this.systemDesignProblems =
        systemDesignProblems == null ? new HashSet<>() : new HashSet<>(systemDesignProblems);
  }

  public int totalConfiguredQuestions() {
    return behavioralQuestionCount + algorithmQuestionCount + systemDesignQuestionCount;
  }
}
