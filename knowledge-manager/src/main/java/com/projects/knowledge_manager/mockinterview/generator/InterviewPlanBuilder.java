package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class InterviewPlanBuilder {

  private static final List<QuestionType> FALLBACK_ORDER =
      List.of(QuestionType.ALGORITHM, QuestionType.BEHAVIORAL, QuestionType.SYSTEM_DESIGN);

  private InterviewPlanBuilder() {}

  public static List<InterviewPlanItem> build(List<QuestionType> pattern, InterviewCandidatePool pool) {
    Set<String> used = new HashSet<>();
    List<InterviewPlanItem> plan = new ArrayList<>();

    for (QuestionType type : pattern) {
      pick(pool.ofType(type), used)
          .or(() -> pickFallback(type, pool, used))
          .ifPresent(
              candidate -> {
                used.add(key(candidate));
                plan.add(
                    new InterviewPlanItem(candidate.type(), candidate.sourceId(), candidate.title()));
              });
    }

    return plan;
  }

  private static java.util.Optional<InterviewCandidate> pickFallback(
      QuestionType preferred, InterviewCandidatePool pool, Set<String> used) {
    for (QuestionType type : FALLBACK_ORDER) {
      if (type == preferred) {
        continue;
      }
      var candidate = pick(pool.ofType(type), used);
      if (candidate.isPresent()) {
        return candidate;
      }
    }
    return java.util.Optional.empty();
  }

  private static java.util.Optional<InterviewCandidate> pick(
      List<InterviewCandidate> candidates, Set<String> used) {
    List<InterviewCandidate> available =
        candidates.stream().filter(candidate -> !used.contains(key(candidate))).toList();
    if (available.isEmpty()) {
      return java.util.Optional.empty();
    }

    int bestScore =
        available.stream().mapToInt(InterviewCandidate::priorityScore).max().orElse(0);
    List<InterviewCandidate> top =
        available.stream().filter(candidate -> candidate.priorityScore() == bestScore).toList();
    return java.util.Optional.of(top.get(ThreadLocalRandom.current().nextInt(top.size())));
  }

  private static String key(InterviewCandidate candidate) {
    return candidate.type().name() + ":" + candidate.sourceId();
  }
}
