package com.projects.knowledge_manager.problem.dto;

import java.util.List;

public record ProblemListPageView(
    List<ProblemSummaryView> content,
    int page,
    int totalPages,
    long totalElements,
    int pageSize) {

  public boolean hasPrevious() {
    return page > 0;
  }

  public boolean hasNext() {
    return page < totalPages - 1;
  }

  public long fromIndex() {
    if (totalElements == 0) {
      return 0;
    }
    return (long) page * pageSize + 1;
  }

  public long toIndex() {
    if (totalElements == 0) {
      return 0;
    }
    return Math.min((long) (page + 1) * pageSize, totalElements);
  }

  public boolean isEmpty() {
    return totalElements == 0;
  }
}
