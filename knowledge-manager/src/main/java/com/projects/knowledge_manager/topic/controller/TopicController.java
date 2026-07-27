package com.projects.knowledge_manager.topic.controller;

import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.topic.service.DuplicateTopicNameException;
import com.projects.knowledge_manager.topic.service.EmptyTopicMarathonException;
import com.projects.knowledge_manager.topic.service.TopicMarathonService;
import com.projects.knowledge_manager.topic.service.TopicService;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics")
public class TopicController {

  private final TopicService topicService;
  private final ProblemService problemService;
  private final TopicMarathonService topicMarathonService;

  public TopicController(
      TopicService topicService,
      ProblemService problemService,
      TopicMarathonService topicMarathonService) {
    this.topicService = topicService;
    this.problemService = problemService;
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
    model.addAttribute("topicForm", TopicForm.empty());
    model.addAttribute("pageTitle", "New Topic");
    model.addAttribute("formAction", "/topics");
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
        topicService.create(topicForm);
        redirectAttributes.addFlashAttribute("successMessage", "Topic created successfully.");
        return "redirect:/topics";
      } catch (DuplicateTopicNameException exception) {
        bindingResult.rejectValue("name", "duplicate", exception.getMessage());
      }
    }

    model.addAttribute("pageTitle", "New Topic");
    model.addAttribute("formAction", "/topics");
    return "topics/form";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("topicForm", topicService.findFormById(id));
    model.addAttribute("pageTitle", "Edit Topic");
    model.addAttribute("formAction", "/topics/" + id);
    model.addAttribute("topicId", id);
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
        return "redirect:/topics";
      } catch (DuplicateTopicNameException exception) {
        bindingResult.rejectValue("name", "duplicate", exception.getMessage());
      }
    }

    model.addAttribute("pageTitle", "Edit Topic");
    model.addAttribute("formAction", "/topics/" + id);
    model.addAttribute("topicId", id);
    return "topics/form";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    topicService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Topic deleted successfully.");
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
}
