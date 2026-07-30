package com.projects.knowledge_manager.review.controller;

import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.topic.service.TopicMarathonService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/problems/{problemId}/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final ProblemService problemService;
  private final TopicMarathonService topicMarathonService;

  public ReviewController(
      ReviewService reviewService,
      ProblemService problemService,
      TopicMarathonService topicMarathonService) {
    this.reviewService = reviewService;
    this.problemService = problemService;
    this.topicMarathonService = topicMarathonService;
  }

  @GetMapping({"/session", "/new"})
  public String reviewSession(@PathVariable Long problemId, Model model, HttpSession session) {
    populateSessionModel(problemId, model, ReviewForm.empty(), session);
    return "reviews/session";
  }

  @PostMapping
  public String create(
      @PathVariable Long problemId,
      @Valid @ModelAttribute("reviewForm") ReviewForm reviewForm,
      BindingResult bindingResult,
      @RequestParam(required = false) String marathonAction,
      Model model,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      populateSessionModel(problemId, model, reviewForm, session);
      return "reviews/session";
    }

    var problem = problemService.findDetailById(problemId);
    reviewService.create(problemId, reviewForm);

    var marathonState =
        topicMarathonService
            .findActive(session)
            .filter(state -> problem.belongsToTopic(state.getTopicId()));

    if (marathonState.isPresent()) {
      var state = marathonState.get();
      topicMarathonService.recordCompletion(
          session,
          problemId,
          problem.title(),
          reviewForm.rating(),
          reviewForm.reviewDuration());

      if ("next".equals(marathonAction)) {
        return topicMarathonService
            .findNextProblemId(state.getTopicId(), state.completedProblemIds())
            .map(nextId -> "redirect:/problems/" + nextId + "/reviews/session")
            .orElseGet(
                () -> {
                  redirectAttributes.addFlashAttribute(
                      "successMessage", "No more problems left in this topic marathon.");
                  return "redirect:/topics/" + state.getTopicId() + "/marathon/summary";
                });
      }

      if ("end".equals(marathonAction)) {
        return "redirect:/topics/" + state.getTopicId() + "/marathon/summary";
      }
    }

    redirectAttributes.addFlashAttribute("successMessage", "Review logged successfully.");
    return "redirect:/dashboard";
  }

  private void populateSessionModel(
      Long problemId, Model model, ReviewForm reviewForm, HttpSession session) {
    var problem = problemService.findDetailById(problemId);
    model.addAttribute("problem", problem);
    model.addAttribute("reviewStatus", reviewService.getProblemReviewStatus(problemId));
    model.addAttribute("reviewForm", reviewForm);
    model.addAttribute("pageTitle", "Review · " + problem.title());

    var marathon =
        topicMarathonService
            .findActive(session)
            .filter(state -> problem.belongsToTopic(state.getTopicId()));
    model.addAttribute("marathon", marathon.orElse(null));
    model.addAttribute("marathonMode", marathon.isPresent());
  }
}
