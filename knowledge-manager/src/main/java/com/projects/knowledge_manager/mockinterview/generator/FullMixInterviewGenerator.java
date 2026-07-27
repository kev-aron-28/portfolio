package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.List;
import org.springframework.stereotype.Component;

/** Full mix: Behavioral → Algorithm → System Design → Algorithm → Behavioral. */
@Component
public class FullMixInterviewGenerator implements InterviewGenerator {

  private static final List<QuestionType> PATTERN =
      List.of(
          QuestionType.BEHAVIORAL,
          QuestionType.ALGORITHM,
          QuestionType.SYSTEM_DESIGN,
          QuestionType.ALGORITHM,
          QuestionType.BEHAVIORAL);

  @Override
  public InterviewFormat format() {
    return InterviewFormat.FULL_MIX;
  }

  @Override
  public List<InterviewPlanItem> generate(InterviewCandidatePool pool) {
    return InterviewPlanBuilder.build(PATTERN, pool);
  }
}
