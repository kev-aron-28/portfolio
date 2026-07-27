package com.projects.knowledge_manager.interviewprofile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionForm;
import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionService;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileForm;
import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemForm;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import com.projects.knowledge_manager.systemdesign.service.SystemDesignProblemService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.service.TopicService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class InterviewProfileServiceTest {

  @Autowired private InterviewProfileService profileService;
  @Autowired private TopicService topicService;
  @Autowired private ProblemService problemService;
  @Autowired private BehavioralQuestionService behavioralQuestionService;
  @Autowired private SystemDesignProblemService systemDesignProblemService;

  private Long problemId;
  private Long behavioralId;
  private Long systemDesignId;

  @BeforeEach
  void setUp() {
    Long topicId = topicService.create(new TopicForm("Arrays", null, "#2563eb")).id();
    problemId =
        problemService
            .create(
                new ProblemForm(
                    "Two Sum",
                    "",
                    Difficulty.EASY,
                    "desc",
                    topicId,
                    List.of(),
                    "",
                    false,
                    false,
                    "java",
                    "code",
                    "explain",
                    "O(n)",
                    ""))
            .id();
    behavioralId =
        behavioralQuestionService
            .create(
                new BehavioralQuestionForm(
                    "Conflict",
                    BehavioralCategory.CONFLICT_RESOLUTION,
                    "Tell me about a conflict.",
                    "S",
                    "T",
                    "A",
                    "R",
                    ""))
            .id();
    systemDesignId =
        systemDesignProblemService
            .create(
                new SystemDesignProblemForm(
                    "Design Twitter",
                    SystemDesignCategory.SOCIAL_NETWORKS,
                    Difficulty.HARD,
                    "Design Twitter",
                    "",
                    45,
                    false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""))
            .id();
  }

  @Test
  void createsDuplicatesAndArchivesProfile() {
    var created =
        profileService.create(
            new InterviewProfileForm(
                "Google SWE",
                "Google-style loop",
                "Google",
                "#4285f4",
                "bi-google",
                1,
                2,
                1,
                60,
                List.of(Difficulty.MEDIUM, Difficulty.HARD),
                List.of(problemId),
                List.of(behavioralId),
                List.of(systemDesignId),
                false,
                "{\"note\":\"future\"}"));

    assertThat(created.name()).isEqualTo("Google SWE");
    assertThat(created.assignedProblemCount()).isEqualTo(1);
    assertThat(created.assignedBehavioralCount()).isEqualTo(1);
    assertThat(created.assignedSystemDesignCount()).isEqualTo(1);
    assertThat(created.targetDifficulties()).containsExactly(Difficulty.HARD, Difficulty.MEDIUM);

    var copy = profileService.duplicate(created.id());
    assertThat(copy.name()).isEqualTo("Google SWE (Copy)");
    assertThat(copy.assignedProblemCount()).isEqualTo(1);
    assertThat(copy.archived()).isFalse();

    profileService.setArchived(created.id(), true);
    assertThat(profileService.findAll(false)).extracting("name").doesNotContain("Google SWE");
    assertThat(profileService.findAll(true)).extracting("name").contains("Google SWE");
  }

  @Test
  void rejectsDuplicateNamesAndEmptyComposition() {
    profileService.create(
        new InterviewProfileForm(
            "Amazon",
            "",
            "Amazon",
            "#ff9900",
            "bi-briefcase",
            1,
            1,
            0,
            45,
            List.of(),
            List.of(problemId),
            List.of(behavioralId),
            List.of(),
            false,
            ""));

    assertThatThrownBy(
            () ->
                profileService.create(
                    new InterviewProfileForm(
                        "amazon",
                        "",
                        "",
                        "#111111",
                        "bi-briefcase",
                        1,
                        0,
                        0,
                        30,
                        List.of(),
                        List.of(),
                        List.of(behavioralId),
                        List.of(),
                        false,
                        "")))
        .isInstanceOf(DuplicateInterviewProfileNameException.class);

    assertThatThrownBy(
            () ->
                profileService.create(
                    new InterviewProfileForm(
                        "Empty",
                        "",
                        "",
                        "#111111",
                        "bi-briefcase",
                        0,
                        0,
                        0,
                        30,
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        false,
                        "")))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
