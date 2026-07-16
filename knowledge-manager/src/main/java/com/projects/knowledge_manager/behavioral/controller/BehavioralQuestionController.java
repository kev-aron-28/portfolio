package com.projects.knowledge_manager.behavioral.controller;

import com.projects.knowledge_manager.behavioral.dto.BehavioralPracticeForm;
import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionForm;
import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionService;
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
@RequestMapping("/behavioral")
public class BehavioralQuestionController {

  private final BehavioralQuestionService questionService;

  public BehavioralQuestionController(BehavioralQuestionService questionService) {
    this.questionService = questionService;
  }

  @GetMapping
  public String list(
      @RequestParam(required = false) BehavioralCategory category,
      @RequestParam(required = false) String q,
      Model model) {
    model.addAttribute("questions", questionService.findFiltered(category, q));
    model.addAttribute("stats", questionService.buildStats());
    model.addAttribute("categories", BehavioralCategory.values());
    model.addAttribute("selectedCategory", category);
    model.addAttribute("searchQuery", q == null ? "" : q);
    model.addAttribute("pageTitle", "Behavioral Practice");
    return "behavioral/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    populateFormModel(model, BehavioralQuestionForm.empty(), "New Behavioral Question", "/behavioral", null);
    return "behavioral/form";
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute("questionForm") BehavioralQuestionForm questionForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      questionService.create(questionForm);
      redirectAttributes.addFlashAttribute("successMessage", "Question created successfully.");
      return "redirect:/behavioral";
    }

    populateFormModel(model, questionForm, "New Behavioral Question", "/behavioral", null);
    return "behavioral/form";
  }

  @GetMapping("/practice")
  public String startPractice(RedirectAttributes redirectAttributes) {
    return questionService
        .findRandomDueQuestion()
        .map(question -> "redirect:/behavioral/" + question.id() + "/practice")
        .orElseGet(
            () -> {
              redirectAttributes.addFlashAttribute(
                  "successMessage", "Nothing due right now. Add a question or check back later.");
              return "redirect:/behavioral";
            });
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Model model) {
    var question = questionService.findById(id);
    model.addAttribute("question", question);
    model.addAttribute("history", questionService.findPracticeHistory(id));
    model.addAttribute("pageTitle", question.title());
    return "behavioral/detail";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    populateFormModel(
        model,
        questionService.findFormById(id),
        "Edit Behavioral Question",
        "/behavioral/" + id,
        id);
    return "behavioral/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("questionForm") BehavioralQuestionForm questionForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      questionService.update(id, questionForm);
      redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully.");
      return "redirect:/behavioral/" + id;
    }

    populateFormModel(model, questionForm, "Edit Behavioral Question", "/behavioral/" + id, id);
    return "behavioral/form";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    questionService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully.");
    return "redirect:/behavioral";
  }

  @GetMapping("/{id}/practice")
  public String practiceSession(@PathVariable Long id, Model model) {
    populatePracticeModel(id, model, BehavioralPracticeForm.empty());
    return "behavioral/practice";
  }

  @PostMapping("/{id}/practices")
  public String recordPractice(
      @PathVariable Long id,
      @Valid @ModelAttribute("practiceForm") BehavioralPracticeForm practiceForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (practiceForm.rating() != null && PracticeRating.fromQuality(practiceForm.rating()) == null) {
      bindingResult.rejectValue(
          "rating", "invalid", "Choose Forgot, Hard, Good, or Easy.");
    }

    if (bindingResult.hasErrors()) {
      populatePracticeModel(id, model, practiceForm);
      model.addAttribute("answerRevealed", true);
      return "behavioral/practice";
    }

    questionService.recordPractice(id, practiceForm);
    redirectAttributes.addFlashAttribute("successMessage", "Practice session saved.");
    return "redirect:/behavioral";
  }

  private void populateFormModel(
      Model model,
      BehavioralQuestionForm form,
      String pageTitle,
      String formAction,
      Long questionId) {
    model.addAttribute("questionForm", form);
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("formAction", formAction);
    model.addAttribute("questionId", questionId);
    model.addAttribute("categories", BehavioralCategory.values());
  }

  private void populatePracticeModel(Long id, Model model, BehavioralPracticeForm practiceForm) {
    var question = questionService.findById(id);
    model.addAttribute("question", question);
    model.addAttribute("practiceForm", practiceForm);
    model.addAttribute("ratings", PracticeRating.values());
    model.addAttribute("answerRevealed", false);
    model.addAttribute("pageTitle", "Practice · " + question.title());
  }
}
