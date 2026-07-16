package com.projects.knowledge_manager.problem.controller;

import com.projects.knowledge_manager.problem.dto.BulkProblemForm;
import com.projects.knowledge_manager.problem.dto.ProblemFilterCriteria;
import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.model.DueStatus;
import com.projects.knowledge_manager.problem.service.BulkProblemValidationException;
import com.projects.knowledge_manager.problem.service.EmptyBulkProblemSubmissionException;
import com.projects.knowledge_manager.problem.service.InvalidTagSelectionException;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.tag.service.InvalidTagNameException;
import com.projects.knowledge_manager.tag.service.TagService;
import com.projects.knowledge_manager.topic.service.TopicService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/problems")
public class ProblemController {

  private final ProblemService problemService;
  private final TopicService topicService;
  private final TagService tagService;
  private final ReviewService reviewService;

  public ProblemController(
      ProblemService problemService,
      TopicService topicService,
      TagService tagService,
      ReviewService reviewService) {
    this.problemService = problemService;
    this.topicService = topicService;
    this.tagService = tagService;
    this.reviewService = reviewService;
  }

  @GetMapping
  public String list(
      @RequestParam(defaultValue = "false") boolean archived,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Long topicId,
      @RequestParam(required = false) Difficulty difficulty,
      @RequestParam(required = false) Long tagId,
      @RequestParam(required = false) Boolean favorite,
      @RequestParam(required = false) DueStatus dueStatus,
      @RequestParam(defaultValue = "0") int page,
      Model model) {

    ProblemFilterCriteria criteria =
        new ProblemFilterCriteria(q, topicId, difficulty, tagId, favorite, dueStatus, archived);

    model.addAttribute("problemPage", problemService.findFilteredPage(criteria, page));
    model.addAttribute("filter", criteria);
    model.addAttribute("showArchived", archived);
    model.addAttribute("topics", topicService.findAll());
    model.addAttribute("tags", tagService.findAll());
    model.addAttribute("difficulties", Difficulty.values());
    model.addAttribute("dueStatuses", DueStatus.values());
    model.addAttribute("pageTitle", archived ? "Archived Problems" : "Problems");
    return "problems/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    populateFormModel(model, ProblemForm.empty(), "New Problem", "/problems", null);
    return "problems/form";
  }

  @GetMapping("/bulk/new")
  public String bulkCreateForm(Model model) {
    populateBulkFormModel(model, BulkProblemForm.empty());
    return "problems/bulk-form";
  }

  @PostMapping("/bulk")
  public String bulkCreate(
      @Valid @ModelAttribute("bulkProblemForm") BulkProblemForm bulkProblemForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      try {
        int created = problemService.createBulk(bulkProblemForm);
        redirectAttributes.addFlashAttribute(
            "successMessage", created + " problems created successfully.");
        return "redirect:/problems";
      } catch (EmptyBulkProblemSubmissionException exception) {
        bindingResult.reject("rows", exception.getMessage());
      } catch (BulkProblemValidationException exception) {
        bindingResult.rejectValue(exception.field(), "invalid", exception.getMessage());
      } catch (InvalidTagSelectionException | InvalidTagNameException exception) {
        bindingResult.reject("rows", exception.getMessage());
      }
    }

    populateBulkFormModel(model, bulkProblemForm);
    return "problems/bulk-form";
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Model model) {
    var problem = problemService.findDetailById(id);
    model.addAttribute("problem", problem);
    model.addAttribute("reviewStatus", reviewService.getProblemReviewStatus(id));
    model.addAttribute("pageTitle", problem.title());
    return "problems/detail";
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute("problemForm") ProblemForm problemForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      try {
        var created = problemService.create(problemForm);
        redirectAttributes.addFlashAttribute("successMessage", "Problem created successfully.");
        return "redirect:/problems/" + created.id();
      } catch (InvalidTagSelectionException exception) {
        bindingResult.reject("tagIds", exception.getMessage());
      } catch (InvalidTagNameException exception) {
        bindingResult.rejectValue("newTagNames", "invalid", exception.getMessage());
      }
    }

    populateFormModel(model, problemForm, "New Problem", "/problems", null);
    return "problems/form";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    populateFormModel(
        model, problemService.findFormById(id), "Edit Problem", "/problems/" + id, id);
    return "problems/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("problemForm") ProblemForm problemForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      try {
        var updated = problemService.update(id, problemForm);
        redirectAttributes.addFlashAttribute("successMessage", "Problem updated successfully.");
        return "redirect:/problems/" + updated.id();
      } catch (InvalidTagSelectionException exception) {
        bindingResult.reject("tagIds", exception.getMessage());
      } catch (InvalidTagNameException exception) {
        bindingResult.rejectValue("newTagNames", "invalid", exception.getMessage());
      }
    }

    populateFormModel(model, problemForm, "Edit Problem", "/problems/" + id, id);
    return "problems/form";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    problemService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Problem deleted successfully.");
    return "redirect:/problems";
  }

  @PostMapping("/{id}/toggle-favorite")
  public String toggleFavorite(@PathVariable Long id) {
    problemService.toggleFavorite(id);
    return "redirect:/problems/" + id;
  }

  @PostMapping("/{id}/toggle-archive")
  public String toggleArchive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    problemService.toggleArchive(id);
    redirectAttributes.addFlashAttribute("successMessage", "Archive status updated.");
    return "redirect:/problems/" + id;
  }

  private void populateFormModel(
      Model model, ProblemForm problemForm, String pageTitle, String formAction, Long problemId) {
    model.addAttribute("problemForm", problemForm);
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("formAction", formAction);
    model.addAttribute("problemId", problemId);
    model.addAttribute("topics", topicService.findAll());
    model.addAttribute("tags", tagService.findAll());
    model.addAttribute("difficulties", Difficulty.values());
  }

  private void populateBulkFormModel(Model model, BulkProblemForm bulkProblemForm) {
    model.addAttribute("bulkProblemForm", bulkProblemForm);
    model.addAttribute("pageTitle", "Add Multiple Problems");
    model.addAttribute("topics", topicService.findAll());
    model.addAttribute("tags", tagService.findAll());
    model.addAttribute("difficulties", Difficulty.values());
    model.addAttribute(
        "languages",
        List.of("java", "javascript", "typescript", "python", "cpp", "go", "rust"));
  }
}
