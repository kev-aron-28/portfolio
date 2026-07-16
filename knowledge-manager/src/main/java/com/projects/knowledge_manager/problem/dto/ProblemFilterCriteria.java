package com.projects.knowledge_manager.problem.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.model.DueStatus;

public record ProblemFilterCriteria(
    String query,
    Long topicId,
    Difficulty difficulty,
    Long tagId,
    Boolean favorite,
    DueStatus dueStatus,
    boolean archived) {

  public static ProblemFilterCriteria defaults(boolean archived) {
    return new ProblemFilterCriteria(null, null, null, null, null, DueStatus.ALL, archived);
  }

  public boolean hasQuery() {
    return query != null && !query.isBlank();
  }

  public boolean hasActiveFilters() {
    return hasQuery()
        || topicId != null
        || difficulty != null
        || tagId != null
        || favorite != null
        || (dueStatus != null && dueStatus != DueStatus.ALL);
  }
}
