package com.projects.knowledge_manager.mockinterview.generator;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class InterviewPlanBuilderTest {

  @Test
  void prefersHigherPriorityCandidatesAndAvoidsDuplicates() {
    LocalDate today = LocalDate.now();
    InterviewCandidate dueHard =
        new InterviewCandidate(
            QuestionType.ALGORITHM, 1L, "Hard Due", today, false, true, false, 1, 130);
    InterviewCandidate easyLater =
        new InterviewCandidate(
            QuestionType.ALGORITHM,
            2L,
            "Easy Later",
            today.plusDays(5),
            false,
            false,
            false,
            5,
            10);
    InterviewCandidate behavioral =
        new InterviewCandidate(
            QuestionType.BEHAVIORAL, 10L, "Conflict", today, false, true, true, null, 120);

    Map<QuestionType, List<InterviewCandidate>> map = new EnumMap<>(QuestionType.class);
    map.put(QuestionType.ALGORITHM, List.of(easyLater, dueHard));
    map.put(QuestionType.BEHAVIORAL, List.of(behavioral));

    List<InterviewPlanItem> plan =
        InterviewPlanBuilder.build(
            List.of(QuestionType.BEHAVIORAL, QuestionType.ALGORITHM, QuestionType.ALGORITHM),
            new InterviewCandidatePool(map));

    assertThat(plan).hasSize(3);
    assertThat(plan.get(0).type()).isEqualTo(QuestionType.BEHAVIORAL);
    assertThat(plan.get(1).sourceId()).isEqualTo(1L);
    assertThat(plan.get(2).sourceId()).isEqualTo(2L);
  }
}
