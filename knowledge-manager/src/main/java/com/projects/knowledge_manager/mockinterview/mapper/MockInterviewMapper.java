package com.projects.knowledge_manager.mockinterview.mapper;

import com.projects.knowledge_manager.mockinterview.dto.MockInterviewItemView;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewSummaryView;
import com.projects.knowledge_manager.mockinterview.entity.MockInterview;
import com.projects.knowledge_manager.mockinterview.entity.MockInterviewItem;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import java.util.List;

public final class MockInterviewMapper {

  private MockInterviewMapper() {}

  public static MockInterviewItemView toItemView(MockInterviewItem item) {
    return new MockInterviewItemView(
        item.getId(),
        item.getItemOrder(),
        item.getQuestionType(),
        item.getProblemId(),
        item.getBehavioralQuestionId(),
        item.getSystemDesignProblemId(),
        item.getTitle(),
        item.getDurationSeconds(),
        item.getRating(),
        item.getCompletedAt(),
        item.isCompleted());
  }

  public static MockInterviewSummaryView toSummaryView(MockInterview interview) {
    return toSummaryView(interview, null);
  }

  public static MockInterviewSummaryView toSummaryView(
      MockInterview interview, String profileName) {
    List<MockInterviewItemView> items =
        interview.getItems().stream().map(MockInterviewMapper::toItemView).toList();
    int behavioral =
        (int)
            items.stream()
                .filter(item -> item.questionType() == QuestionType.BEHAVIORAL)
                .count();
    int algorithms =
        (int)
            items.stream()
                .filter(item -> item.questionType() == QuestionType.ALGORITHM)
                .count();
    int systemDesign =
        (int)
            items.stream()
                .filter(item -> item.questionType() == QuestionType.SYSTEM_DESIGN)
                .count();
    double average =
        items.stream()
            .filter(MockInterviewItemView::completed)
            .mapToInt(item -> item.durationSeconds() == null ? 0 : item.durationSeconds())
            .average()
            .orElse(0);

    return new MockInterviewSummaryView(
        interview.getId(),
        interview.getFormat(),
        interview.getProfileId(),
        profileName,
        interview.getStartedAt(),
        interview.getFinishedAt(),
        interview.getTotalDurationSeconds(),
        interview.getTotalQuestions(),
        behavioral,
        algorithms,
        systemDesign,
        average,
        items);
  }
}
