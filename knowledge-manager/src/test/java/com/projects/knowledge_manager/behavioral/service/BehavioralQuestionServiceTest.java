package com.projects.knowledge_manager.behavioral.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.behavioral.dto.BehavioralPracticeForm;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionForm;
import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BehavioralQuestionServiceTest {

  @Autowired private BehavioralQuestionService questionService;

  @Test
  void createsFiltersAndPracticesQuestion() {
    var created =
        questionService.create(
            new BehavioralQuestionForm(
                "Difficult teammate",
                BehavioralCategory.CONFLICT_RESOLUTION,
                "Tell me about a time you had to deal with a difficult teammate.",
                "On a delivery team…",
                "I needed to unblock the release.",
                "I set a 1:1 and clarified ownership.",
                "We shipped on time.",
                "Keep feedback private."));

    assertThat(created.title()).isEqualTo("Difficult teammate");
    assertThat(created.dueToday()).isTrue();
    assertThat(created.neverPracticed()).isTrue();

    assertThat(
            questionService.findFiltered(
                BehavioralCategory.CONFLICT_RESOLUTION, "difficult"))
        .hasSize(1);

    var practice =
        questionService.recordPractice(
            created.id(),
            new BehavioralPracticeForm(
                LocalDate.now(), 125, PracticeRating.GOOD.getQuality()));

    assertThat(practice.durationSeconds()).isEqualTo(125);
    assertThat(practice.rating()).isEqualTo(4);
    assertThat(practice.nextReviewDate()).isAfter(LocalDate.now().minusDays(1));

    var after = questionService.findById(created.id());
    assertThat(after.neverPracticed()).isFalse();
    assertThat(questionService.buildStats().totalPractices()).isEqualTo(1);
  }

  @Test
  void rejectsInvalidPracticeRating() {
    var created =
        questionService.create(
            new BehavioralQuestionForm(
                "Failure story",
                BehavioralCategory.FAILURE,
                "Tell me about a time you failed.",
                "",
                "",
                "",
                "",
                ""));

    assertThatThrownBy(
            () ->
                questionService.recordPractice(
                    created.id(), new BehavioralPracticeForm(LocalDate.now(), 30, 2)))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
