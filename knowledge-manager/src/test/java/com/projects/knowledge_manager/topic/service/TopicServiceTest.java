package com.projects.knowledge_manager.topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.dto.TopicView;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TopicServiceTest {

  @Autowired private TopicService topicService;
  @Autowired private ProblemService problemService;

  @Test
  void createsAndFindsTopic() {
    TopicView created =
        topicService.create(new TopicForm("Arrays", "Array problems", "#ef4444"));

    assertThat(created.name()).isEqualTo("Arrays");
    assertThat(created.description()).isEqualTo("Array problems");
    assertThat(created.color()).isEqualTo("#ef4444");
    assertThat(topicService.findAll()).hasSize(1);
  }

  @Test
  void rejectsDuplicateNames() {
    topicService.create(new TopicForm("Trees", null, "#22c55e"));

    assertThatThrownBy(() -> topicService.create(new TopicForm("trees", null, "#22c55e")))
        .isInstanceOf(DuplicateTopicNameException.class);
  }

  @Test
  void linksExistingProblemsWithoutRemovingOtherTopics() {
    TopicView arrays = topicService.create(new TopicForm("Arrays", null, "#ef4444"));
    TopicView dp = topicService.create(new TopicForm("DP", null, "#22c55e"));

    var problem =
        problemService.create(
            new ProblemForm(
                "Two Sum",
                "",
                Difficulty.EASY,
                "",
                List.of(arrays.id()),
                List.of(),
                "",
                false,
                false,
                "java",
                "",
                "",
                "",
                ""));

    topicService.update(dp.id(), new TopicForm("DP", null, "#22c55e", List.of(problem.id())));

    var detail = problemService.findDetailById(problem.id());
    assertThat(detail.topics()).extracting(topic -> topic.id()).containsExactlyInAnyOrder(arrays.id(), dp.id());
    assertThat(topicService.findFormById(arrays.id()).problemIds()).containsExactly(problem.id());
    assertThat(topicService.findFormById(dp.id()).problemIds()).containsExactly(problem.id());
  }

  @Test
  void rejectsUnlinkingLastTopicFromProblem() {
    TopicView arrays = topicService.create(new TopicForm("Arrays", null, "#ef4444"));
    var problem =
        problemService.create(
            new ProblemForm(
                "Two Sum",
                "",
                Difficulty.EASY,
                "",
                List.of(arrays.id()),
                List.of(),
                "",
                false,
                false,
                "java",
                "",
                "",
                "",
                ""));

    assertThatThrownBy(
            () ->
                topicService.update(
                    arrays.id(), new TopicForm("Arrays", null, "#ef4444", List.of())))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("at least one topic");

    assertThat(problemService.findDetailById(problem.id()).topics())
        .extracting(topic -> topic.id())
        .containsExactly(arrays.id());
  }
}
