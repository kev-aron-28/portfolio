package com.projects.knowledge_manager.problem.mapper;

import com.projects.knowledge_manager.problem.dto.ProblemDetailView;
import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.dto.ProblemSummaryView;
import com.projects.knowledge_manager.problem.dto.SolutionView;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.entity.Solution;
import com.projects.knowledge_manager.tag.mapper.TagMapper;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProblemMapper {

  private ProblemMapper() {}

  public static ProblemSummaryView toSummaryView(Problem problem) {
    return toSummaryView(problem, null, false);
  }

  public static ProblemSummaryView toSummaryView(
      Problem problem, LocalDate nextReviewDate, boolean overdue) {
    return new ProblemSummaryView(
        problem.getId(),
        problem.getTitle(),
        problem.getUrl(),
        problem.getDifficulty(),
        problem.isFavorite(),
        problem.isArchived(),
        problem.getTopic().getId(),
        problem.getTopic().getName(),
        problem.getTopic().getColor(),
        problem.getTags().stream()
            .map(TagMapper::toView)
            .sorted(Comparator.comparing(tagView -> tagView.name().toLowerCase()))
            .toList(),
        nextReviewDate,
        overdue,
        problem.getUpdatedAt());
  }

  public static ProblemDetailView toDetailView(Problem problem) {
    return new ProblemDetailView(
        problem.getId(),
        problem.getTitle(),
        problem.getUrl(),
        problem.getDifficulty(),
        problem.getDescription(),
        problem.isFavorite(),
        problem.isArchived(),
        problem.getTopic().getId(),
        problem.getTopic().getName(),
        problem.getTopic().getColor(),
        problem.getTags().stream()
            .map(TagMapper::toView)
            .sorted(Comparator.comparing(tagView -> tagView.name().toLowerCase()))
            .toList(),
        toSolutionView(problem.getSolution()),
        problem.getCreatedAt(),
        problem.getUpdatedAt());
  }

  public static ProblemForm toForm(Problem problem) {
    Solution solution = problem.getSolution();
    return new ProblemForm(
        problem.getTitle(),
        problem.getUrl(),
        problem.getDifficulty(),
        problem.getDescription(),
        problem.getTopic().getId(),
        problem.getTags().stream().map(tag -> tag.getId()).sorted().toList(),
        "",
        problem.isFavorite(),
        problem.isArchived(),
        solution != null ? solution.getLanguage() : "java",
        solution != null ? nullToEmpty(solution.getSourceCode()) : "",
        solution != null ? nullToEmpty(solution.getExplanation()) : "",
        solution != null ? nullToEmpty(solution.getComplexity()) : "",
        solution != null ? nullToEmpty(solution.getMistakes()) : "");
  }

  public static void updateEntity(Problem problem, ProblemForm form, Set<com.projects.knowledge_manager.tag.entity.Tag> tags) {
    problem.setTitle(form.title().trim());
    problem.setUrl(normalizeOptional(form.url()));
    problem.setDifficulty(form.difficulty());
    problem.setDescription(normalizeOptional(form.description()));
    problem.setFavorite(form.favorite());
    problem.setArchived(form.archived());
    problem.getTags().clear();
    problem.getTags().addAll(tags);
    updateSolution(problem, form);
  }

  public static Problem toEntity(
      ProblemForm form,
      com.projects.knowledge_manager.topic.entity.Topic topic,
      Set<com.projects.knowledge_manager.tag.entity.Tag> tags) {
    Problem problem = new Problem(form.title().trim(), form.difficulty(), topic);
    problem.setUrl(normalizeOptional(form.url()));
    problem.setDescription(normalizeOptional(form.description()));
    problem.setFavorite(form.favorite());
    problem.setArchived(form.archived());
    problem.setTags(new HashSet<>(tags));
    Solution solution = new Solution(problem);
    updateSolutionFields(solution, form);
    problem.setSolution(solution);
    return problem;
  }

  private static void updateSolution(Problem problem, ProblemForm form) {
    Solution solution = problem.getSolution();
    if (solution == null) {
      solution = new Solution(problem);
      problem.setSolution(solution);
    }
    updateSolutionFields(solution, form);
  }

  private static void updateSolutionFields(Solution solution, ProblemForm form) {
    solution.setLanguage(form.language().trim());
    solution.setSourceCode(normalizeOptional(form.sourceCode()));
    solution.setExplanation(normalizeOptional(form.explanation()));
    solution.setComplexity(normalizeOptional(form.complexity()));
    solution.setMistakes(normalizeOptional(form.mistakes()));
  }

  private static SolutionView toSolutionView(Solution solution) {
    if (solution == null) {
      return new SolutionView("java", "", "", "", "");
    }
    return new SolutionView(
        solution.getLanguage(),
        nullToEmpty(solution.getSourceCode()),
        nullToEmpty(solution.getExplanation()),
        nullToEmpty(solution.getComplexity()),
        nullToEmpty(solution.getMistakes()));
  }

  private static String normalizeOptional(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}
