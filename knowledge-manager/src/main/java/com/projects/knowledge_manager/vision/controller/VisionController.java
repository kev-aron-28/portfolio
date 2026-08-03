package com.projects.knowledge_manager.vision.controller;

import com.projects.knowledge_manager.vision.dto.SceneSaveRequest;
import com.projects.knowledge_manager.vision.dto.VisionBoardForm;
import com.projects.knowledge_manager.vision.service.VisionBoardService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vision")
public class VisionController {

  private final VisionBoardService visionBoardService;

  public VisionController(VisionBoardService visionBoardService) {
    this.visionBoardService = visionBoardService;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("boards", visionBoardService.findAll());
    model.addAttribute("pageTitle", "Vision");
    return "vision/list";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    model.addAttribute("boardForm", VisionBoardForm.empty());
    model.addAttribute("pageTitle", "New Vision Board");
    model.addAttribute("formAction", "/vision");
    return "vision/form";
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute("boardForm") VisionBoardForm boardForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (!bindingResult.hasErrors()) {
      var created = visionBoardService.create(boardForm);
      redirectAttributes.addFlashAttribute("successMessage", "Vision board created.");
      return "redirect:/vision/" + created.id();
    }
    model.addAttribute("pageTitle", "New Vision Board");
    model.addAttribute("formAction", "/vision");
    return "vision/form";
  }

  @GetMapping("/{id}")
  public String canvas(@PathVariable Long id, Model model) {
    var board = visionBoardService.findById(id);
    model.addAttribute("board", board);
    model.addAttribute("pageTitle", board.title());
    return "vision/canvas";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("boardForm", visionBoardService.findFormById(id));
    model.addAttribute("pageTitle", "Rename Vision Board");
    model.addAttribute("formAction", "/vision/" + id);
    model.addAttribute("boardId", id);
    return "vision/form";
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute("boardForm") VisionBoardForm boardForm,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (!bindingResult.hasErrors()) {
      visionBoardService.update(id, boardForm);
      redirectAttributes.addFlashAttribute("successMessage", "Vision board updated.");
      return "redirect:/vision/" + id;
    }
    model.addAttribute("pageTitle", "Rename Vision Board");
    model.addAttribute("formAction", "/vision/" + id);
    model.addAttribute("boardId", id);
    return "vision/form";
  }

  @PostMapping("/{id}/scene")
  @ResponseBody
  public ResponseEntity<Map<String, String>> saveScene(
      @PathVariable Long id, @RequestBody SceneSaveRequest request) {
    visionBoardService.saveScene(id, request.sceneJson());
    return ResponseEntity.ok(Map.of("status", "saved"));
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    visionBoardService.delete(id);
    redirectAttributes.addFlashAttribute("successMessage", "Vision board deleted.");
    return "redirect:/vision";
  }
}
