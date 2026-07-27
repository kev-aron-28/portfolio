package com.projects.knowledge_manager.systemdesign.controller;

import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemForm;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignReviewForm;
import com.projects.knowledge_manager.systemdesign.dto.WhiteboardSaveRequest;
import com.projects.knowledge_manager.systemdesign.model.ReviewStatusFilter;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import com.projects.knowledge_manager.systemdesign.service.SystemDesignProblemService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/system-design")
public class SystemDesignProblemController {

  private final SystemDesignProblemService problemService;

  public SystemDesignProblemController(SystemDesignProblemService problemService) {
    this.problemService = problemService;
  }

  @GetMapping
  public String list(
      @RequestParam(required = false) SystemDesignCategory category,
      @RequestParam(required = false) Difficulty difficulty,
      @RequestParam(required = false) String tag,
      @RequestParam(required = false) ReviewStatusFilter status,
      @RequestParam(required = false) Boolean dueToday,
      @RequestParam(required = false) Boolean favorites,
      @RequestParam(required = false) String q,
      Model model) {
    model.addAttribute(
        "problems",
        problemService.findFiltered(category, difficulty, tag, status, dueToday, favorites, q));
    model.addAttribute("stats", problemService.buildStats());
    model.addAttribute("categories", SystemDesignCategory.values());
    model.addAttribute("difficulties", Difficulty.values());
    model.addAttribute("statuses", ReviewStatusFilter.values());
    model.addAttribute("selectedCategory", category);
    model.addAttribute("selectedDifficulty", difficulty);
    model.addAttribute("selectedTag", tag == null ? "" : tag);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("dueTodayOnly", Boolean.TRUE.equals(dueToday));
    model.addAttribute("favoritesOnly", Boolean.TRUE.equals(favorites));
    model.addAttribute("searchQuery", q == null ? "" : q);
    model.addAttribute("pageTitle", "System Design");
    return "systemdesign/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    populateFormModel(
        model, SystemDesignProblemForm.empty(), "New System Design", "/system-design", null);
    return "systemdesign/form";
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute("problemForm") SystemDesignProblemForm problemForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (!bindingResult.hasErrors()) {
      var created = problemService.create(problemForm);
      redirectAttributes.addFlashAttribute("successMessage", "Design created successfully.");
      return "redirect:/system-design/" + created.id();
    }
    populateFormModel(model, problemForm, "New System Design", "/system-design", null);
    return "systemdesign/form";
  }

  @GetMapping("/practice")
  public String startPractice(RedirectAttributes redirectAttributes) {
    return problemService
        .findRandomDueProblem()
        .map(problem -> "redirect:/system-design/" + problem.id() + "/practice")
        .orElseGet(
            () -> {
              redirectAttributes.addFlashAttribute(
                  "successMessage", "Nothing due right now. Add a design or check back later.");
              return "redirect:/system-design";
            });
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Model model) {
    var problem = problemService.findById(id);
    model.addAttribute("problem", problem);
    model.addAttribute("history", problemService.findReviewHistory(id));
    model.addAttribute("pageTitle", problem.title());
    return "systemdesign/detail";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    populateFormModel(
        model,
        problemService.findFormById(id),
        "Edit System Design",
        "/system-design/" + id,
        id);
    return "systemdesign/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("problemForm") SystemDesignProblemForm problemForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (!bindingResult.hasErrors()) {
      problemService.update(id, problemForm);
      redirectAttributes.addFlashAttribute("successMessage", "Design updated successfully.");
      return "redirect:/system-design/" + id;
    }
    populateFormModel(model, problemForm, "Edit System Design", "/system-design/" + id, id);
    return "systemdesign/form";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    problemService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Design deleted successfully.");
    return "redirect:/system-design";
  }

  @GetMapping("/{id}/practice")
  public String practiceSession(@PathVariable Long id, Model model) {
    populatePracticeModel(id, model, SystemDesignReviewForm.empty());
    return "systemdesign/practice";
  }

  @PostMapping("/{id}/reviews")
  public String recordReview(
      @PathVariable Long id,
      @Valid @ModelAttribute("reviewForm") SystemDesignReviewForm reviewForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (reviewForm.rating() != null && PracticeRating.fromQuality(reviewForm.rating()) == null) {
      bindingResult.rejectValue("rating", "invalid", "Choose Forgot, Hard, Good, or Easy.");
    }

    if (bindingResult.hasErrors()) {
      populatePracticeModel(id, model, reviewForm);
      model.addAttribute("answerRevealed", true);
      return "systemdesign/practice";
    }

    problemService.recordReview(id, reviewForm);
    redirectAttributes.addFlashAttribute("successMessage", "Review saved.");
    return "redirect:/system-design";
  }

  @PostMapping("/{id}/whiteboard")
  @ResponseBody
  public ResponseEntity<Map<String, String>> saveWhiteboard(
      @PathVariable Long id, @RequestBody WhiteboardSaveRequest request) {
    problemService.saveWhiteboard(id, request.sceneJson());
    return ResponseEntity.ok(Map.of("status", "saved"));
  }

  private void populateFormModel(
      Model model,
      SystemDesignProblemForm form,
      String pageTitle,
      String formAction,
      Long problemId) {
    model.addAttribute("problemForm", form);
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("formAction", formAction);
    model.addAttribute("problemId", problemId);
    model.addAttribute("categories", SystemDesignCategory.values());
    model.addAttribute("difficulties", Difficulty.values());
  }

  private void populatePracticeModel(Long id, Model model, SystemDesignReviewForm reviewForm) {
    var problem = problemService.findById(id);
    model.addAttribute("problem", problem);
    model.addAttribute("reviewForm", reviewForm);
    model.addAttribute("ratings", PracticeRating.values());
    model.addAttribute("answerRevealed", false);
    model.addAttribute("pageTitle", "Practice · " + problem.title());
  }
}
