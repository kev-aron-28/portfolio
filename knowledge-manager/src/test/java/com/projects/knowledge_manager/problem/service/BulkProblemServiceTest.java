package com.projects.knowledge_manager.problem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.problem.dto.BulkProblemForm;
import com.projects.knowledge_manager.problem.dto.BulkProblemRowForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.tag.service.TagService;
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
class BulkProblemServiceTest {

  @Autowired private ProblemService problemService;
  @Autowired private TopicService topicService;
  @Autowired private TagService tagService;

  private Long arraysTopicId;
  private Long graphsTopicId;

  @BeforeEach
  void setUp() {
    arraysTopicId = topicService.create(new TopicForm("Arrays", null, "#ef4444")).id();
    graphsTopicId = topicService.create(new TopicForm("Graphs", null, "#3b82f6")).id();
  }

  @Test
  void createsMultipleProblemsWithPerRowTopicTagsAndSolution() {
    int created =
        problemService.createBulk(
            new BulkProblemForm(
                List.of(
                    new BulkProblemRowForm(
                        "Two Sum",
                        "https://leetcode.com/problems/two-sum",
                        arraysTopicId,
                        Difficulty.EASY,
                        "Find two numbers that add up to target.",
                        "java",
                        "class Solution { }",
                        List.of(),
                        "Two Pointers"),
                    new BulkProblemRowForm(
                        "Number of Islands",
                        "",
                        graphsTopicId,
                        Difficulty.MEDIUM,
                        "Count connected components in a grid.",
                        "java",
                        "void dfs() {}",
                        List.of(),
                        "BFS, DFS"),
                    BulkProblemRowForm.empty())));

    assertThat(created).isEqualTo(2);
    assertThat(tagService.findAll()).hasSize(3);
    assertThat(problemService.findAll(false)).hasSize(2);
  }

  @Test
  void rejectsRowMissingDescription() {
    assertThatThrownBy(
            () ->
                problemService.createBulk(
                    new BulkProblemForm(
                        List.of(
                            new BulkProblemRowForm(
                                "Two Sum",
                                "",
                                arraysTopicId,
                                Difficulty.EASY,
                                "",
                                "java",
                                "class Solution {}",
                                List.of(),
                                "")))))
        .isInstanceOf(BulkProblemValidationException.class)
        .hasMessageContaining("Description is required");
  }

  @Test
  void rejectsRowMissingSolution() {
    assertThatThrownBy(
            () ->
                problemService.createBulk(
                    new BulkProblemForm(
                        List.of(
                            new BulkProblemRowForm(
                                "Two Sum",
                                "",
                                arraysTopicId,
                                Difficulty.EASY,
                                "Some description",
                                "java",
                                "",
                                List.of(),
                                "")))))
        .isInstanceOf(BulkProblemValidationException.class)
        .hasMessageContaining("Solution code is required");
  }
}
