package com.projects.knowledge_manager.problem.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.dto.ProblemFilterCriteria;
import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.model.DueStatus;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.tag.dto.TagForm;
import com.projects.knowledge_manager.tag.service.TagService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.service.TopicService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProblemFilterServiceTest {

  @Autowired private ProblemService problemService;
  @Autowired private TopicService topicService;
  @Autowired private TagService tagService;
  @Autowired private ReviewService reviewService;

  private Long arraysTopicId;
  private Long graphsTopicId;
  private Long bfsTagId;

  @BeforeEach
  void setUp() {
    arraysTopicId = topicService.create(new TopicForm("Arrays", null, "#ef4444")).id();
    graphsTopicId = topicService.create(new TopicForm("Graphs", null, "#6366f1")).id();
    bfsTagId = tagService.create(new TagForm("BFS")).id();

    problemService.create(
        new ProblemForm(
            "Two Sum",
            "",
            Difficulty.EASY,
            "classic array problem",
            arraysTopicId,
            List.of(bfsTagId),
            "",
            true,
            false,
            "java",
            "",
            "",
            "",
            ""));

    problemService.create(
        new ProblemForm(
            "Number of Islands",
            "",
            Difficulty.MEDIUM,
            "graph traversal",
            graphsTopicId,
            List.of(),
            "",
            false,
            false,
            "java",
            "",
            "",
            "",
            ""));
  }

  @Test
  void filtersBySearchTopicAndFavorite() {
    var results =
        problemService.findFiltered(
            new ProblemFilterCriteria("array", arraysTopicId, null, null, true, DueStatus.ALL, false));

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().title()).isEqualTo("Two Sum");
  }

  @Test
  void filtersNeverReviewedProblems() {
    var results =
        problemService.findFiltered(
            new ProblemFilterCriteria(null, null, null, null, null, DueStatus.NEVER_REVIEWED, false));

    assertThat(results).hasSize(2);
  }

  @Test
  void excludesReviewedProblemsFromNeverReviewedFilter() {
    var created =
        problemService.findFiltered(
            new ProblemFilterCriteria("Islands", null, null, null, null, DueStatus.ALL, false));
    reviewService.create(
        created.getFirst().id(), new ReviewForm(LocalDate.now(), 4, 10, "Good"));

    var neverReviewed =
        problemService.findFiltered(
            new ProblemFilterCriteria(null, null, null, null, null, DueStatus.NEVER_REVIEWED, false));

    assertThat(neverReviewed).hasSize(1);
    assertThat(neverReviewed.getFirst().title()).isEqualTo("Two Sum");
  }

  @Test
  void paginatesFilteredResults() {
    for (int index = 0; index < 5; index++) {
      problemService.create(
          new ProblemForm(
              "Problem " + index,
              "",
              Difficulty.EASY,
              "",
              arraysTopicId,
              List.of(),
              "",
              false,
              false,
              "java",
              "",
              "",
              "",
              ""));
    }

    var firstPage =
        problemService.findFilteredPage(
            new ProblemFilterCriteria(null, null, null, null, null, DueStatus.ALL, false), 0);
    var secondPage =
        problemService.findFilteredPage(
            new ProblemFilterCriteria(null, null, null, null, null, DueStatus.ALL, false), 1);

    assertThat(firstPage.totalElements()).isEqualTo(7);
    assertThat(firstPage.content()).hasSize(3);
    assertThat(firstPage.totalPages()).isEqualTo(3);
    assertThat(secondPage.page()).isEqualTo(1);
    assertThat(secondPage.content()).hasSize(3);
    assertThat(firstPage.fromIndex()).isEqualTo(1);
    assertThat(firstPage.toIndex()).isEqualTo(3);
  }
}
