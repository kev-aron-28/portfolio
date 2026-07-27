package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BehavioralOnlyInterviewGenerator implements InterviewGenerator {

  private static final List<QuestionType> PATTERN =
      List.of(
          QuestionType.BEHAVIORAL,
          QuestionType.BEHAVIORAL,
          QuestionType.BEHAVIORAL,
          QuestionType.BEHAVIORAL);

  @Override
  public InterviewFormat format() {
    return InterviewFormat.BEHAVIORAL_ONLY;
  }

  @Override
  public List<InterviewPlanItem> generate(InterviewCandidatePool pool) {
    return InterviewPlanBuilder.build(PATTERN, pool);
  }
}
