package com.projects.knowledge_manager.mockinterview.generator;

import com.projects.knowledge_manager.interviewprofile.entity.InterviewProfile;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a question-type pattern from profile configuration.
 * Keeps the mock interview engine data-driven (no company-specific logic).
 */
public final class ProfileInterviewPatternBuilder {

  private ProfileInterviewPatternBuilder() {}

  public static List<QuestionType> build(InterviewProfile profile) {
    List<QuestionType> pattern = new ArrayList<>();
    append(pattern, QuestionType.BEHAVIORAL, profile.getBehavioralQuestionCount());
    append(pattern, QuestionType.ALGORITHM, profile.getAlgorithmQuestionCount());
    append(pattern, QuestionType.SYSTEM_DESIGN, profile.getSystemDesignQuestionCount());
    return pattern;
  }

  private static void append(List<QuestionType> pattern, QuestionType type, int count) {
    for (int i = 0; i < count; i++) {
      pattern.add(type);
    }
  }
}
