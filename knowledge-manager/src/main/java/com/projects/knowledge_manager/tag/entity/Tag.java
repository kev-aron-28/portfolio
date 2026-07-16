package com.projects.knowledge_manager.tag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tags")
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100, unique = true)
  private String name;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected Tag() {}

  public Tag(String name) {
    this.name = name;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
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

  public Instant getCreatedAt() {
    return createdAt;
  }
}
