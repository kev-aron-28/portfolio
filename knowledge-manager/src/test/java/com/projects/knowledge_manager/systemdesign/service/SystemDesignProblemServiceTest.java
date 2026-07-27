package com.projects.knowledge_manager.systemdesign.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemForm;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignReviewForm;
import com.projects.knowledge_manager.systemdesign.model.ReviewStatusFilter;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SystemDesignProblemServiceTest {

  @Autowired private SystemDesignProblemService problemService;

  @Test
  void createsFiltersReviewsAndSavesWhiteboard() {
    var created =
        problemService.create(
            new SystemDesignProblemForm(
                "Design Twitter",
                SystemDesignCategory.SOCIAL_NETWORKS,
                Difficulty.HARD,
                "Design a Twitter-like timeline and tweet posting system.",
                "https://example.com/twitter",
                45,
                true,
                "feed, fanout",
                "Microblogging platform",
                "Post tweets, follow users, read timeline",
                "Low latency reads",
                "Eventually consistent timelines OK",
                "API gateway, tweet service, fanout workers",
                "Tweet service, timeline cache, media store",
                "Users, tweets, follows",
                "POST /tweets, GET /timeline",
                "Shard by user id",
                "Redis timeline cache",
                "LB in front of API",
                "Async fanout queue",
                "Push vs pull fanout",
                "Hot celebrity timelines",
                "Prefer hybrid fanout",
                "Review celebrity edge cases"));

    assertThat(created.title()).isEqualTo("Design Twitter");
    assertThat(created.dueToday()).isTrue();
    assertThat(created.neverReviewed()).isTrue();
    assertThat(created.tags()).containsExactly("fanout", "feed");
    assertThat(created.favorite()).isTrue();

    assertThat(
            problemService.findFiltered(
                SystemDesignCategory.SOCIAL_NETWORKS,
                Difficulty.HARD,
                "feed",
                ReviewStatusFilter.NEVER_REVIEWED,
                true,
                true,
                "twitter"))
        .hasSize(1);

    problemService.saveWhiteboard(created.id(), "{\"version\":\"5.3.0\",\"objects\":[]}");
    assertThat(problemService.findById(created.id()).whiteboardJson())
        .contains("\"objects\":[]");

    var review =
        problemService.recordReview(
            created.id(),
            new SystemDesignReviewForm(
                LocalDate.now(), 600, PracticeRating.GOOD.getQuality()));

    assertThat(review.durationSeconds()).isEqualTo(600);
    assertThat(review.rating()).isEqualTo(4);
    assertThat(review.nextReviewDate()).isAfter(LocalDate.now().minusDays(1));

    var after = problemService.findById(created.id());
    assertThat(after.neverReviewed()).isFalse();
    assertThat(problemService.buildStats().totalReviews()).isEqualTo(1);
    assertThat(problemService.buildStats().totalDesigns()).isEqualTo(1);
  }

  @Test
  void rejectsInvalidReviewRating() {
    var created =
        problemService.create(
            new SystemDesignProblemForm(
                "Design Rate Limiter",
                SystemDesignCategory.INFRASTRUCTURE,
                Difficulty.MEDIUM,
                "Design a distributed rate limiter.",
                "",
                30,
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
                ""));

    assertThatThrownBy(
            () ->
                problemService.recordReview(
                    created.id(), new SystemDesignReviewForm(LocalDate.now(), 30, 2)))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
