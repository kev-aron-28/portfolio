package com.projects.knowledge_manager.tag.controller;

import com.projects.knowledge_manager.tag.dto.TagForm;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.tag.service.DuplicateTagNameException;
import com.projects.knowledge_manager.tag.service.TagService;
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
@RequestMapping("/tags")
public class TagController {

  private final TagService tagService;
  private final ProblemService problemService;

  public TagController(TagService tagService, ProblemService problemService) {
    this.tagService = tagService;
    this.problemService = problemService;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("tagGroups", problemService.findGroupedByTag(false));
    model.addAttribute("pageTitle", "Tags");
    return "tags/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    model.addAttribute("tagForm", TagForm.empty());
    model.addAttribute("pageTitle", "New Tag");
    model.addAttribute("formAction", "/tags");
    return "tags/form";
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute("tagForm") TagForm tagForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      try {
        tagService.create(tagForm);
        redirectAttributes.addFlashAttribute("successMessage", "Tag created successfully.");
        return "redirect:/tags";
      } catch (DuplicateTagNameException exception) {
        bindingResult.rejectValue("name", "duplicate", exception.getMessage());
      }
    }

    model.addAttribute("pageTitle", "New Tag");
    model.addAttribute("formAction", "/tags");
    return "tags/form";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("tagForm", tagService.findFormById(id));
    model.addAttribute("pageTitle", "Edit Tag");
    model.addAttribute("formAction", "/tags/" + id);
    return "tags/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("tagForm") TagForm tagForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (!bindingResult.hasErrors()) {
      try {
        tagService.update(id, tagForm);
        redirectAttributes.addFlashAttribute("successMessage", "Tag updated successfully.");
        return "redirect:/tags";
      } catch (DuplicateTagNameException exception) {
        bindingResult.rejectValue("name", "duplicate", exception.getMessage());
      }
    }

    model.addAttribute("pageTitle", "Edit Tag");
    model.addAttribute("formAction", "/tags/" + id);
    return "tags/form";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    tagService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Tag deleted successfully.");
    return "redirect:/tags";
  }
}
