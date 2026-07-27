package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmsOnlyInterviewGenerator implements InterviewGenerator {

  private static final List<QuestionType> PATTERN =
      List.of(
          QuestionType.ALGORITHM,
          QuestionType.ALGORITHM,
          QuestionType.ALGORITHM,
          QuestionType.ALGORITHM);

  @Override
  public InterviewFormat format() {
    return InterviewFormat.ALGORITHMS_ONLY;
  }

  @Override
  public List<InterviewPlanItem> generate(InterviewCandidatePool pool) {
    return InterviewPlanBuilder.build(PATTERN, pool);
  }
}
