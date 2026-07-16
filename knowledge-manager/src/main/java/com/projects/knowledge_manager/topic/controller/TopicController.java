package com.projects.knowledge_manager.topic.controller;

import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.topic.service.DuplicateTopicNameException;
import com.projects.knowledge_manager.topic.service.TopicService;
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
@RequestMapping("/topics")
public class TopicController {

  private final TopicService topicService;
  private final ProblemService problemService;

  public TopicController(TopicService topicService, ProblemService problemService) {
    this.topicService = topicService;
    this.problemService = problemService;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("topicGroups", problemService.findGroupedByTopic(false));
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
}
