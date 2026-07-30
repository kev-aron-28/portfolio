package com.projects.knowledge_manager.topic.controller;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.dto.TopicQuickCreateProblemRequest;
import com.projects.knowledge_manager.topic.dto.TopicQuickCreateProblemResponse;
import com.projects.knowledge_manager.topic.service.DuplicateTopicNameException;
import com.projects.knowledge_manager.topic.service.EmptyTopicMarathonException;
import com.projects.knowledge_manager.topic.service.TopicMarathonService;
import com.projects.knowledge_manager.topic.service.TopicNotFoundException;
import com.projects.knowledge_manager.topic.service.TopicService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics")
public class TopicController {

  private final TopicService topicService;
  private final ProblemService problemService;
  private final ProblemRepository problemRepository;
  private final TopicMarathonService topicMarathonService;

  public TopicController(
      TopicService topicService,
      ProblemService problemService,
      ProblemRepository problemRepository,
      TopicMarathonService topicMarathonService) {
    this.topicService = topicService;
    this.problemService = problemService;
    this.problemRepository = problemRepository;
    this.topicMarathonService = topicMarathonService;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("topicGroups", problemService.findGroupedByTopic(false));
    model.addAttribute("topicReviewMinutes", topicMarathonService.totalReviewMinutesByTopic());
    model.addAttribute("pageTitle", "Topics");
    return "topics/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    populateFormModel(model, TopicForm.empty(), "New Topic", "/topics", null);
    return "topics/form";
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute("topicForm") TopicForm topicForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      try {
        var created = topicService.create(topicForm);
        redirectAttributes.addFlashAttribute(
            "successMessage", "Topic created. You can add or create problems below.");
        return "redirect:/topics/" + created.id() + "/edit";
      } catch (DuplicateTopicNameException | IllegalArgumentException exception) {
        bindingResult.rejectValue("name", "invalid", exception.getMessage());
      }
    }

    populateFormModel(model, topicForm, "New Topic", "/topics", null);
    return "topics/form";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    populateFormModel(
        model, topicService.findFormById(id), "Edit Topic", "/topics/" + id, id);
    return "topics/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("topicForm") TopicForm topicForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      try {
        topicService.update(id, topicForm);
        redirectAttributes.addFlashAttribute("successMessage", "Topic updated successfully.");
        return "redirect:/topics/" + id + "/edit";
      } catch (DuplicateTopicNameException | IllegalArgumentException exception) {
        bindingResult.rejectValue("name", "invalid", exception.getMessage());
      }
    }

    populateFormModel(model, topicForm, "Edit Topic", "/topics/" + id, id);
    return "topics/form";
  }

  @PostMapping("/{id}/quick-create/problem")
  @ResponseBody
  public ResponseEntity<?> quickCreateProblem(
      @PathVariable Long id,
      @Valid @RequestBody TopicQuickCreateProblemRequest request,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      String message =
          bindingResult.getFieldErrors().stream()
              .findFirst()
              .map(error -> error.getDefaultMessage())
              .orElse("Invalid request");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }

    try {
      topicService.findById(id);
    } catch (TopicNotFoundException exception) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("message", exception.getMessage()));
    }

    var created =
        problemService.create(
            new ProblemForm(
                request.title().trim(),
                blankToEmpty(request.url()),
                request.difficulty(),
                blankToEmpty(request.description()),
                List.of(id),
                List.of(),
                "",
                false,
                false,
                "java",
                "",
                "",
                "",
                ""));

    return ResponseEntity.ok(
        new TopicQuickCreateProblemResponse(
            created.id(), created.title(), created.difficulty().name(), created.topicName()));
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      topicService.delete(id);
      redirectAttributes.addFlashAttribute("successMessage", "Topic deleted successfully.");
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("successMessage", exception.getMessage());
    }
    return "redirect:/topics";
  }

  @GetMapping("/{id}/marathon")
  public String startMarathon(
      @PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
    try {
      var state = topicMarathonService.start(id, session);
      return topicMarathonService
          .findNextProblemId(state.getTopicId(), List.of())
          .map(problemId -> "redirect:/problems/" + problemId + "/reviews/session")
          .orElseGet(
              () -> {
                topicMarathonService.clear(session);
                redirectAttributes.addFlashAttribute(
                    "successMessage", "No problems available in this topic.");
                return "redirect:/topics";
              });
    } catch (EmptyTopicMarathonException exception) {
      redirectAttributes.addFlashAttribute("successMessage", exception.getMessage());
      return "redirect:/topics";
    }
  }

  @GetMapping("/{id}/marathon/summary")
  public String marathonSummary(@PathVariable Long id, HttpSession session, Model model) {
    var active = topicMarathonService.findActiveForTopic(session, id);
    if (active.isEmpty()) {
      return "redirect:/topics";
    }

    var state = topicMarathonService.end(session);
    model.addAttribute("marathon", state);
    model.addAttribute("topicTotalMinutes", topicMarathonService.totalReviewMinutesForTopic(id));
    model.addAttribute("pageTitle", "Marathon · " + state.getTopicName());
    return "topics/marathon-summary";
  }

  private void populateFormModel(
      Model model, TopicForm form, String pageTitle, String formAction, Long topicId) {
    model.addAttribute("topicForm", form);
    model.addAttribute("pageTitle", pageTitle);
    model.addAttribute("formAction", formAction);
    model.addAttribute("topicId", topicId);
    model.addAttribute("difficulties", Difficulty.values());
    model.addAttribute("problems", problemRepository.findAllByArchivedFalseOrderByTitleAsc());
  }

  private static String blankToEmpty(String value) {
    return value == null ? "" : value.trim();
  }
}
