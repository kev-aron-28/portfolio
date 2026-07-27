package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SystemDesignOnlyInterviewGenerator implements InterviewGenerator {

  private static final List<QuestionType> PATTERN =
      List.of(
          QuestionType.SYSTEM_DESIGN,
          QuestionType.SYSTEM_DESIGN,
          QuestionType.SYSTEM_DESIGN,
          QuestionType.SYSTEM_DESIGN);

  @Override
  public InterviewFormat format() {
    return InterviewFormat.SYSTEM_DESIGN_ONLY;
  }

  @Override
  public List<InterviewPlanItem> generate(InterviewCandidatePool pool) {
    return InterviewPlanBuilder.build(PATTERN, pool);
  }
}
