package com.projects.knowledge_manager.mockinterview.controller;

import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import com.projects.knowledge_manager.interviewprofile.service.InterviewProfileService;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewAnswerForm;
import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.service.EmptyInterviewPlanException;
import com.projects.knowledge_manager.mockinterview.service.MockInterviewService;
import jakarta.validation.Valid;
import java.util.Arrays;
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
@RequestMapping("/mock-interviews")
public class MockInterviewController {

  private final MockInterviewService interviewService;
  private final InterviewProfileService profileService;

  public MockInterviewController(
      MockInterviewService interviewService, InterviewProfileService profileService) {
    this.interviewService = interviewService;
    this.profileService = profileService;
  }

  @GetMapping
  public String hub(Model model) {
    model.addAttribute("stats", interviewService.buildStats());
    model.addAttribute("history", interviewService.findHistory());
    model.addAttribute(
        "formats",
        Arrays.stream(InterviewFormat.values())
            .filter(format -> format != InterviewFormat.PROFILE_DRIVEN)
            .toList());
    model.addAttribute("profiles", profileService.findAll(false));
    model.addAttribute("profileStats", profileService.buildStatsByProfile());
    model.addAttribute("pageTitle", "Mock Interviews");
    return "mockinterview/hub";
  }

  @PostMapping("/start")
  public String start(
      @RequestParam(required = false) Long profileId,
      @RequestParam(required = false) InterviewFormat format,
      RedirectAttributes redirectAttributes) {
    try {
      var interview =
          profileId != null
              ? interviewService.startWithProfile(profileId)
              : interviewService.start(format == null ? InterviewFormat.STANDARD_MIX : format);
      return "redirect:/mock-interviews/" + interview.id() + "/session";
    } catch (EmptyInterviewPlanException | IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute(
          "successMessage",
          exception.getMessage() == null
              ? "Could not build an interview plan."
              : exception.getMessage());
      return "redirect:/mock-interviews";
    }
  }

  @GetMapping("/{id}/session")
  public String session(@PathVariable Long id, Model model) {
    if (interviewService.findCurrentItem(id).isEmpty()) {
      return "redirect:/mock-interviews/" + id + "/summary";
    }
    populateSession(model, id, MockInterviewAnswerForm.empty(), false);
    return "mockinterview/session";
  }

  @PostMapping("/{id}/answer")
  public String answer(
      @PathVariable Long id,
      @Valid @ModelAttribute("answerForm") MockInterviewAnswerForm answerForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (answerForm.rating() != null && PracticeRating.fromQuality(answerForm.rating()) == null) {
      bindingResult.rejectValue("rating", "invalid", "Choose Forgot, Hard, Good, or Easy.");
    }

    if (bindingResult.hasErrors()) {
      populateSession(model, id, answerForm, true);
      return "mockinterview/session";
    }

    boolean finished = interviewService.completeCurrentItem(id, answerForm);
    if (finished) {
      redirectAttributes.addFlashAttribute("successMessage", "Interview complete.");
      return "redirect:/mock-interviews/" + id + "/summary";
    }
    return "redirect:/mock-interviews/" + id + "/session";
  }

  @GetMapping("/{id}/summary")
  public String summary(@PathVariable Long id, Model model) {
    model.addAttribute("summary", interviewService.getSummary(id));
    model.addAttribute("pageTitle", "Interview Summary");
    return "mockinterview/summary";
  }

  private void populateSession(
      Model model, Long id, MockInterviewAnswerForm form, boolean revealAnswer) {
    model.addAttribute("interviewSession", interviewService.buildSession(id, revealAnswer));
    model.addAttribute("answerForm", form);
    model.addAttribute("ratings", PracticeRating.values());
    model.addAttribute("pageTitle", "Mock Interview");
  }
}
