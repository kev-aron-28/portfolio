package com.projects.knowledge_manager.learning;

import com.projects.knowledge_manager.learning.LearningService.Activity;
import com.projects.knowledge_manager.learning.LearningService.BoardItem;
import com.projects.knowledge_manager.learning.LearningService.Edge;
import com.projects.knowledge_manager.learning.LearningService.Kind;
import com.projects.knowledge_manager.learning.LearningService.Node;
import com.projects.knowledge_manager.learning.LearningService.Section;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/learning")
public class LearningController {
  public record TreeSummary(LearningService.Tree tree, long completed, long total, long due, long seconds) {}

  public record SectionProgress(int completed, int total) {
    SectionProgress plus(SectionProgress other) {
      return new SectionProgress(completed + other.completed, total + other.total);
    }
  }

  public enum NodeStatus {
    LOCKED("Bloqueado", "bi-lock"),
    AVAILABLE("Disponible", "bi-unlock"),
    ACTIVE("En estudio", "bi-play"),
    COMPLETE("Completado", "bi-check2"),
    DUE("Por repasar", "bi-arrow-repeat");

    private final String label;
    private final String icon;

    NodeStatus(String label, String icon) {
      this.label = label;
      this.icon = icon;
    }

    public String getLabel() {
      return label;
    }

    public String getIcon() {
      return icon;
    }
  }

  private final LearningService learning;

  public LearningController(LearningService learning) {
    this.learning = learning;
  }

  @ModelAttribute("learningLocale")
  Locale learningLocale() {
    return Locale.forLanguageTag("es-MX");
  }

  @GetMapping
  String home(Model model) {
    model.addAttribute("pageTitle", "Aprendizaje");
    model.addAttribute("today", learning.today());
    List<Node> due = learning.due();
    List<LearningService.Tree> trees = learning.trees();
    Map<Long, String> treeTitles = new HashMap<>();
    trees.forEach(tree -> treeTitles.put(tree.id(), tree.title()));
    model.addAttribute("treeTitles", treeTitles);
    model.addAttribute("due", due);
    var stats = learning.stats();
    long power = stats.understanding() + stats.dexterity() + stats.application() + stats.mastery() * 5;
    int stage = power >= 25 ? 3 : power >= 14 ? 2 : power >= 6 ? 1 : 0;
    model.addAttribute("stats", stats);
    model.addAttribute("power", power);
    model.addAttribute("avatarStage", stage);
    var ranks = List.of("Aprendiz", "Explorador", "Veterano", "Maestro");
    model.addAttribute("avatarRank", ranks.get(stage));
    model.addAttribute("nextRank", ranks.get(Math.min(stage + 1, ranks.size() - 1)));
    model.addAttribute("nextPower", List.of(6, 14, 25, 25).get(stage));
    model.addAttribute("trees", trees.stream().map(tree -> {
      List<Node> nodes = learning.nodes(tree.id()).stream().filter(node -> node.kind() != Kind.MASTERY).toList();
      return new TreeSummary(tree, nodes.stream().filter(node -> node.completedOn() != null).count(),
          nodes.size(), due.stream().filter(node -> node.treeId() == tree.id()).count(),
          learning.time(tree.id(), null).seconds());
    }).toList());
    model.addAttribute("time", learning.time(null, null));
    model.addAttribute("focus", learning.activeFocus());
    return "learning/home";
  }

  @GetMapping("/{treeId}")
  String tree(@PathVariable long treeId, @RequestParam(required = false) Long selected,
              @RequestParam(required = false) Long section, Model model) {
    var tree = learning.tree(treeId);
    List<Node> nodes = learning.nodes(treeId);
    List<Edge> edges = learning.edges(treeId);
    Node selectedNode = selected == null ? null : LearningService.find(nodes, selected);
    Long currentSectionId = selectedNode == null ? section : selectedNode.sectionId();
    Section currentSection = addSections(model, learning.sections(treeId), nodes, currentSectionId);
    model.addAttribute("tree", tree);
    model.addAttribute("boardPositionLimit", LearningService.BOARD_POSITION_LIMIT);
    model.addAttribute("pageTitle", (currentSection == null ? tree.title() : currentSection.title()) + " · Aprendizaje");

    List<Node> visibleNodes = nodes.stream().filter(node -> Objects.equals(node.sectionId(), currentSectionId)).toList();
    Set<Long> visibleIds = new HashSet<>();
    visibleNodes.forEach(node -> visibleIds.add(node.id()));
    List<Edge> visibleEdges = edges.stream()
        .filter(edge -> visibleIds.contains(edge.nodeId()) && visibleIds.contains(edge.prerequisiteId())).toList();
    Map<Long, Integer> levels = new HashMap<>();
    for (Node node : visibleNodes) {
      int level = visibleEdges.stream().filter(edge -> edge.nodeId() == node.id())
          .mapToInt(edge -> levels.get(edge.prerequisiteId())).max().orElse(-1) + 1;
      levels.put(node.id(), level);
    }
    visibleNodes.stream().filter(node -> node.kind() == Kind.MASTERY).findFirst()
        .ifPresent(node -> levels.put(node.id(), levels.values().stream().mapToInt(Integer::intValue).max().orElse(0) + 1));
    model.addAttribute("levels", levels.values().stream().distinct().sorted()
        .map(level -> visibleNodes.stream().filter(node -> levels.get(node.id()).equals(level)).toList()).toList());
    model.addAttribute("visibleEdges", visibleEdges);
    model.addAttribute("visibleNodes", visibleNodes);

    Set<Long> ready = new HashSet<>();
    Map<Long, NodeStatus> statuses = new HashMap<>();
    int points = learning.points(nodes);
    LocalDate today = learning.today();
    for (Node node : visibleNodes) {
      if (learning.ready(node, nodes, edges)) {
        ready.add(node.id());
      }
      statuses.put(node.id(), status(node, today, ready.contains(node.id()) && points > 0));
    }
    model.addAttribute("nodes", nodes);
    model.addAttribute("ready", ready);
    model.addAttribute("statuses", statuses);
    model.addAttribute("points", points);
    model.addAttribute("time", currentSection == null ? learning.time(treeId, null) : learning.sectionTime(currentSection.id()));
    model.addAttribute("focus", learning.activeFocus());
    model.addAttribute("kinds", Kind.values());
    model.addAttribute("hasMastery", nodes.stream().anyMatch(node -> node.kind() == Kind.MASTERY));
    model.addAttribute("today", today);
    if (selectedNode != null) {
      model.addAttribute("node", selectedNode);
      model.addAttribute("nodeTime", learning.time(treeId, selected));
      model.addAttribute("requiredEdges", edges.stream()
          .filter(edge -> edge.nodeId() == selected && edge.required()).map(Edge::prerequisiteId).toList());
      model.addAttribute("recommendedEdges", edges.stream()
          .filter(edge -> edge.nodeId() == selected && !edge.required()).map(Edge::prerequisiteId).toList());
      model.addAttribute("history", learning.history(selected));
      Set<Long> branch = new HashSet<>();
      branch.add(selected);
      for (Node candidate : nodes) {
        if (edges.stream().anyMatch(edge -> edge.required() && edge.nodeId() == candidate.id()
            && branch.contains(edge.prerequisiteId()))) {
          branch.add(candidate.id());
        }
      }
      model.addAttribute("branchTime", learning.branchTime(branch));
    }
    return "learning/tree";
  }

  private Section addSections(Model model, List<Section> sections, List<Node> nodes, Long currentId) {
    Map<Long, Section> byId = new HashMap<>();
    Map<Long, String> paths = new HashMap<>();
    for (Section section : sections) {
      byId.put(section.id(), section);
      String path = section.parentId() == null ? section.title()
          : paths.get(section.parentId()) + " / " + section.title();
      paths.put(section.id(), path);
    }
    Section current = byId.get(currentId);
    if (currentId != null && current == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    ArrayDeque<Section> trail = new ArrayDeque<>();
    for (Section ancestor = current; ancestor != null; ancestor = byId.get(ancestor.parentId())) {
      trail.addFirst(ancestor);
    }

    var empty = new SectionProgress(0, 0);
    Map<Long, SectionProgress> progress = new HashMap<>();
    progress.put(null, empty);
    sections.forEach(section -> progress.put(section.id(), empty));
    for (Node node : nodes) {
      if (node.kind() != Kind.MASTERY) {
        progress.merge(node.sectionId(), new SectionProgress(node.completedOn() == null ? 0 : 1, 1),
            SectionProgress::plus);
      }
    }
    for (Section section : sections.reversed()) {
      progress.merge(section.parentId(), progress.get(section.id()), SectionProgress::plus);
    }

    model.addAttribute("currentSection", current);
    model.addAttribute("sections", sections);
    model.addAttribute("sectionTrail", List.copyOf(trail));
    model.addAttribute("sectionChildren", sections.stream()
        .filter(section -> Objects.equals(section.parentId(), currentId)).toList());
    model.addAttribute("sectionPaths", paths);
    model.addAttribute("sectionProgress", progress);
    model.addAttribute("total", progress.get(currentId).total());
    model.addAttribute("completed", progress.get(currentId).completed());
    return current;
  }

  private NodeStatus status(Node node, LocalDate today, boolean available) {
    if (node.completedOn() != null) {
      return node.nextReviewOn() != null && !node.nextReviewOn().isAfter(today)
          ? NodeStatus.DUE : NodeStatus.COMPLETE;
    }
    if (node.unlocked()) {
      return NodeStatus.ACTIVE;
    }
    return available ? NodeStatus.AVAILABLE : NodeStatus.LOCKED;
  }

  @PostMapping
  String create(@RequestParam String title, RedirectAttributes flash) {
    try {
      return redirect(learning.createTree(title), null, null);
    } catch (ResponseStatusException error) {
      flash.addFlashAttribute("error", error.getReason());
      return "redirect:/learning";
    }
  }

  @PostMapping("/{treeId}/delete")
  String delete(@PathVariable long treeId, RedirectAttributes flash) {
    learning.deleteTree(treeId);
    flash.addFlashAttribute("successMessage", "Árbol eliminado.");
    return "redirect:/learning";
  }

  @PostMapping("/{treeId}/sections")
  String saveSection(@PathVariable long treeId, @RequestParam(required = false) Long id,
                     @RequestParam(required = false) Long parentId, @RequestParam String title,
                     RedirectAttributes flash) {
    try {
      return redirect(treeId, null, learning.saveSection(treeId, id, parentId, title));
    } catch (ResponseStatusException error) {
      if (error.getStatusCode().value() != 400) {
        throw error;
      }
      flash.addFlashAttribute("error", error.getReason());
      return redirect(treeId, null, id == null ? parentId : id);
    }
  }

  @PostMapping("/{treeId}/board-position")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void saveBoardPosition(@PathVariable long treeId, @RequestParam(required = false) Long board,
                         @RequestParam BoardItem type, @RequestParam long item,
                         @RequestParam int x, @RequestParam int y) {
    learning.saveBoardPosition(treeId, board, type, item, x, y);
  }

  @PostMapping("/{treeId}/nodes")
  String save(@PathVariable long treeId, @RequestParam(required = false) Long id,
              @RequestParam(required = false) Long sectionId, @RequestParam(required = false) Long boardSection,
              @RequestParam String title, @RequestParam(defaultValue = "TOPIC") Kind kind,
              @RequestParam String criterion, @RequestParam String prompt,
              @RequestParam(required = false) Set<Long> prerequisites,
              @RequestParam(required = false) Set<Long> recommended, RedirectAttributes flash) {
    try {
      learning.saveNode(treeId, id, sectionId, title, kind, criterion, prompt,
          prerequisites == null ? Set.of() : prerequisites, recommended == null ? Set.of() : recommended);
      return redirect(treeId, id, sectionId);
    } catch (ResponseStatusException error) {
      if (error.getStatusCode().value() != 400) {
        throw error;
      }
      flash.addFlashAttribute("error", error.getReason());
      return redirect(treeId, id, boardSection);
    }
  }

  @PostMapping("/{treeId}/nodes/{id}/unlock")
  String unlock(@PathVariable long treeId, @PathVariable long id, RedirectAttributes flash) {
    return change(treeId, id, flash, () -> learning.unlock(treeId, id));
  }

  @PostMapping("/{treeId}/nodes/{id}/complete")
  String complete(@PathVariable long treeId, @PathVariable long id,
                  @RequestParam(defaultValue = "") String evidence,
                  @RequestParam(defaultValue = "false") boolean prior, RedirectAttributes flash) {
    return change(treeId, id, flash, () -> learning.complete(treeId, id, evidence, prior));
  }

  @PostMapping("/{treeId}/nodes/{id}/review")
  String review(@PathVariable long treeId, @PathVariable long id, @RequestParam LocalDate scheduledOn,
                @RequestParam int rating, RedirectAttributes flash) {
    return change(treeId, id, flash, () -> learning.review(treeId, id, scheduledOn, rating));
  }

  @PostMapping("/{treeId}/nodes/{id}/focus")
  String start(@PathVariable long treeId, @PathVariable long id, @RequestParam Activity activity,
               RedirectAttributes flash) {
    return change(treeId, id, flash, () -> learning.startFocus(treeId, id, activity));
  }

  @PostMapping("/focus/{id}/stop")
  String stop(@PathVariable long id, @RequestParam(required = false) Long treeId,
              @RequestParam(required = false) Long selected, @RequestParam(required = false) Long section) {
    learning.stopFocus(id);
    return treeId == null ? "redirect:/learning" : redirect(treeId, selected, section);
  }

  private String change(long treeId, Long id, RedirectAttributes flash, Runnable operation) {
    try {
      operation.run();
    } catch (ResponseStatusException error) {
      if (error.getStatusCode().value() != 400) {
        throw error;
      }
      flash.addFlashAttribute("error", error.getReason());
    }
    return redirect(treeId, id, null);
  }

  private String redirect(long treeId, Long nodeId, Long sectionId) {
    String path = "redirect:/learning/" + treeId;
    if (nodeId != null) {
      return path + "?selected=" + nodeId + (sectionId == null ? "" : "&section=" + sectionId) + "#node-detail";
    }
    return sectionId == null ? path : path + "?section=" + sectionId;
  }
}
