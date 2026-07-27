package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.List;
import java.util.Map;

/** Pool of scored candidates available when building an interview. */
public record InterviewCandidatePool(
    Map<QuestionType, List<InterviewCandidate>> candidatesByType) {

  public List<InterviewCandidate> ofType(QuestionType type) {
    return candidatesByType.getOrDefault(type, List.of());
  }
}
