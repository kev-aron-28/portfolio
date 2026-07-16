package com.projects.knowledge_manager.review.controller;

import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/problems/{problemId}/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final ProblemService problemService;

  public ReviewController(ReviewService reviewService, ProblemService problemService) {
    this.reviewService = reviewService;
    this.problemService = problemService;
  }

  @GetMapping({"/session", "/new"})
  public String reviewSession(@PathVariable Long problemId, Model model) {
    populateSessionModel(problemId, model, ReviewForm.empty());
    return "reviews/session";
  }

  @PostMapping
  public String create(
      @PathVariable Long problemId,
      @Valid @ModelAttribute("reviewForm") ReviewForm reviewForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      populateSessionModel(problemId, model, reviewForm);
      return "reviews/session";
    }

    reviewService.create(problemId, reviewForm);
    redirectAttributes.addFlashAttribute("successMessage", "Review logged successfully.");
    return "redirect:/dashboard";
  }

  private void populateSessionModel(Long problemId, Model model, ReviewForm reviewForm) {
    var problem = problemService.findDetailById(problemId);
    model.addAttribute("problem", problem);
    model.addAttribute("reviewStatus", reviewService.getProblemReviewStatus(problemId));
    model.addAttribute("reviewForm", reviewForm);
    model.addAttribute("pageTitle", "Review · " + problem.title());
  }
}
