package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import java.util.List;

/**
 * Strategy for assembling an interview plan from scored candidates.
 *
 * <p>Independent from the spaced-repetition scheduler: the scheduler marks what is due; generators
 * decide how to shape an interview from that pool.
 */
public interface InterviewGenerator {

  InterviewFormat format();

  List<InterviewPlanItem> generate(InterviewCandidatePool pool);
}
