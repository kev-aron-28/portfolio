package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.List;
import org.springframework.stereotype.Component;

/** Default realistic mix: Behavioral → Algorithm → Algorithm → Behavioral. */
@Component
public class StandardMixInterviewGenerator implements InterviewGenerator {

  private static final List<QuestionType> PATTERN =
      List.of(
          QuestionType.BEHAVIORAL,
          QuestionType.ALGORITHM,
          QuestionType.ALGORITHM,
          QuestionType.BEHAVIORAL);

  @Override
  public InterviewFormat format() {
    return InterviewFormat.STANDARD_MIX;
  }

  @Override
  public List<InterviewPlanItem> generate(InterviewCandidatePool pool) {
    return InterviewPlanBuilder.build(PATTERN, pool);
  }
}
