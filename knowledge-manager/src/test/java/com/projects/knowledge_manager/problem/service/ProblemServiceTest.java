package com.projects.knowledge_manager.problem.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.tag.dto.TagForm;
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
class ProblemServiceTest {

  @Autowired private ProblemService problemService;
  @Autowired private TopicService topicService;
  @Autowired private TagService tagService;

  private Long topicId;
  private Long tagId;

  @BeforeEach
  void setUp() {
    topicId = topicService.create(new TopicForm("Arrays", "Array problems", "#ef4444")).id();
    tagId = tagService.create(new TagForm("Two Pointers")).id();
  }

  @Test
  void createsProblemWithSolutionAndTags() {
    var form =
        new ProblemForm(
            "Two Sum",
            "https://leetcode.com/problems/two-sum",
            Difficulty.EASY,
            "Find two numbers that add up to target.",
            topicId,
            List.of(tagId),
            "",
            true,
            false,
            "java",
            "class Solution {}",
            "Use a hash map.",
            "O(n) time",
            "Forgetting edge cases");

    var created = problemService.create(form);

    assertThat(created.title()).isEqualTo("Two Sum");
    assertThat(created.tags()).hasSize(1);
    assertThat(created.solution().sourceCode()).contains("class Solution");
    assertThat(problemService.findAll(false)).hasSize(1);
  }

  @Test
  void togglesFavoriteAndArchive() {
    var created =
        problemService.create(
            new ProblemForm(
                "Reverse Linked List",
                "",
                Difficulty.MEDIUM,
                "",
                topicId,
                List.of(),
                "",
                false,
                false,
                "java",
                "",
                "",
                "",
                ""));

    problemService.toggleFavorite(created.id());
    problemService.toggleArchive(created.id());

    var updated = problemService.findDetailById(created.id());
    assertThat(updated.favorite()).isTrue();
    assertThat(updated.archived()).isTrue();
    assertThat(problemService.findAll(true)).hasSize(1);
    assertThat(problemService.findAll(false)).isEmpty();
  }

  @Test
  void groupsProblemsByTopicAndTag() {
    var created =
        problemService.create(
            new ProblemForm(
                "Two Sum",
                "",
                Difficulty.EASY,
                "Find two numbers.",
                topicId,
                List.of(tagId),
                "",
                false,
                false,
                "java",
                "class Solution {}",
                "",
                "",
                ""));

    var topicGroups = problemService.findGroupedByTopic(false);
    assertThat(topicGroups).isNotEmpty();
    assertThat(
            topicGroups.stream()
                .filter(group -> group.id().equals(topicId))
                .findFirst()
                .orElseThrow()
                .problems())
        .extracting(summary -> summary.id())
        .containsExactly(created.id());

    var tagGroups = problemService.findGroupedByTag(false);
    assertThat(
            tagGroups.stream()
                .filter(group -> group.id().equals(tagId))
                .findFirst()
                .orElseThrow()
                .problems())
        .extracting(summary -> summary.id())
        .containsExactly(created.id());
  }
}
