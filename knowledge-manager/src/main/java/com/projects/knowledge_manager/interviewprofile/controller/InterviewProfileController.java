package com.projects.knowledge_manager.interviewprofile.controller;

import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import com.projects.knowledge_manager.behavioral.repository.BehavioralQuestionRepository;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileForm;
import com.projects.knowledge_manager.interviewprofile.service.DuplicateInterviewProfileNameException;
import com.projects.knowledge_manager.interviewprofile.service.InterviewProfileService;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.systemdesign.repository.SystemDesignProblemRepository;
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
@RequestMapping("/interview-profiles")
public class InterviewProfileController {

  private final InterviewProfileService profileService;
  private final ProblemRepository problemRepository;
  private final BehavioralQuestionRepository behavioralQuestionRepository;
  private final SystemDesignProblemRepository systemDesignProblemRepository;

  public InterviewProfileController(
      InterviewProfileService profileService,
      ProblemRepository problemRepository,
      BehavioralQuestionRepository behavioralQuestionRepository,
      SystemDesignProblemRepository systemDesignProblemRepository) {
    this.profileService = profileService;
    this.problemRepository = problemRepository;
    this.behavioralQuestionRepository = behavioralQuestionRepository;
    this.systemDesignProblemRepository = systemDesignProblemRepository;
  }

  @GetMapping
  public String list(
      @RequestParam(defaultValue = "false") boolean includeArchived, Model model) {
    model.addAttribute("profiles", profileService.findAll(includeArchived));
    model.addAttribute("profileStats", profileService.buildStatsByProfile());
    model.addAttribute("includeArchived", includeArchived);
    model.addAttribute("pageTitle", "Interview Profiles");
    return "interviewprofile/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    populateFormModel(model, InterviewProfileForm.empty(), "New Interview Profile", "/interview-profiles", null);
    return "interviewprofile/form";
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute("profileForm") InterviewProfileForm profileForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      populateFormModel(model, profileForm, "New Interview Profile", "/interview-profiles", null);
      return "interviewprofile/form";
    }
    try {
      var created = profileService.create(profileForm);
      redirectAttributes.addFlashAttribute("successMessage", "Profile created.");
      return "redirect:/interview-profiles/" + created.id() + "/edit";
    } catch (DuplicateInterviewProfileNameException | IllegalArgumentException exception) {
      bindingResult.rejectValue("name", "invalid", exception.getMessage());
      populateFormModel(model, profileForm, "New Interview Profile", "/interview-profiles", null);
      return "interviewprofile/form";
    }
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Model model) {
    model.addAttribute("profile", profileService.findById(id));
    model.addAttribute(
        "stats",
        profileService.buildStatsByProfile().stream()
            .filter(stat -> stat.profileId().equals(id))
            .findFirst()
            .orElse(null));
    model.addAttribute("pageTitle", profileService.findById(id).name());
    return "interviewprofile/detail";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    populateFormModel(
        model,
        profileService.findFormById(id),
        "Edit Interview Profile",
        "/interview-profiles/" + id,
        id);
    return "interviewprofile/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("profileForm") InterviewProfileForm profileForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      populateFormModel(model, profileForm, "Edit Interview Profile", "/interview-profiles/" + id, id);
      return "interviewprofile/form";
    }
    try {
      profileService.update(id, profileForm);
      redirectAttributes.addFlashAttribute("successMessage", "Profile updated.");
      return "redirect:/interview-profiles/" + id;
    } catch (DuplicateInterviewProfileNameException | IllegalArgumentException exception) {
      bindingResult.rejectValue("name", "invalid", exception.getMessage());
      populateFormModel(model, profileForm, "Edit Interview Profile", "/interview-profiles/" + id, id);
      return "interviewprofile/form";
    }
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    profileService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Profile deleted.");
    return "redirect:/interview-profiles";
  }

  @PostMapping("/{id}/duplicate")
  public String duplicate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    var copy = profileService.duplicate(id);
    redirectAttributes.addFlashAttribute("successMessage", "Profile duplicated.");
    return "redirect:/interview-profiles/" + copy.id() + "/edit";
  }

  @PostMapping("/{id}/archive")
  public String archive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    profileService.setArchived(id, true);
    redirectAttributes.addFlashAttribute("successMessage", "Profile archived.");
    return "redirect:/interview-profiles";
  }

  @PostMapping("/{id}/unarchive")
  public String unarchive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    profileService.setArchived(id, false);
    redirectAttributes.addFlashAttribute("successMessage", "Profile restored.");
    return "redirect:/interview-profiles";
  }

  private void populateFormModel(
      Model model,
      InterviewProfileForm form,
      String pageTitle,
      String formAction,
      Long profileId) {
    model.addAttribute("profileForm", form);
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("formAction", formAction);
    model.addAttribute("profileId", profileId);
    model.addAttribute("difficulties", Difficulty.values());
    model.addAttribute("behavioralCategories", BehavioralCategory.values());
    model.addAttribute("problems", problemRepository.findAllByArchivedFalseOrderByTitleAsc());
    model.addAttribute(
        "behavioralQuestions", behavioralQuestionRepository.findAllByOrderByTitleAsc());
    model.addAttribute(
        "systemDesignProblems", systemDesignProblemRepository.findAllByOrderByTitleAsc());
  }
}
