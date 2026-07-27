package com.projects.knowledge_manager.interviewprofile.mapper;

import com.projects.knowledge_manager.behavioral.entity.BehavioralQuestion;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileForm;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileSummaryView;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileView;
import com.projects.knowledge_manager.interviewprofile.entity.InterviewProfile;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignProblem;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class InterviewProfileMapper {

  private InterviewProfileMapper() {}

  public static InterviewProfile toEntity(
      InterviewProfileForm form,
      Set<Problem> problems,
      Set<BehavioralQuestion> behavioralQuestions,
      Set<SystemDesignProblem> systemDesignProblems) {
    InterviewProfile profile =
        new InterviewProfile(form.name().trim(), form.color().trim(), form.icon().trim());
    applyFields(profile, form, problems, behavioralQuestions, systemDesignProblems);
    return profile;
  }

  public static void updateEntity(
      InterviewProfile profile,
      InterviewProfileForm form,
      Set<Problem> problems,
      Set<BehavioralQuestion> behavioralQuestions,
      Set<SystemDesignProblem> systemDesignProblems) {
    applyFields(profile, form, problems, behavioralQuestions, systemDesignProblems);
  }

  public static InterviewProfileForm toForm(InterviewProfile profile) {
    return new InterviewProfileForm(
        profile.getName(),
        nullToEmpty(profile.getDescription()),
        nullToEmpty(profile.getCompany()),
        profile.getColor(),
        profile.getIcon(),
        profile.getBehavioralQuestionCount(),
        profile.getAlgorithmQuestionCount(),
        profile.getSystemDesignQuestionCount(),
        profile.getMaxDurationMinutes(),
        profile.getTargetDifficulties().stream()
            .sorted(Comparator.comparing(Enum::name))
            .toList(),
        profile.getProblems().stream().map(Problem::getId).sorted().toList(),
        profile.getBehavioralQuestions().stream()
            .map(BehavioralQuestion::getId)
            .sorted()
            .toList(),
        profile.getSystemDesignProblems().stream()
            .map(SystemDesignProblem::getId)
            .sorted()
            .toList(),
        profile.isArchived(),
        nullToEmpty(profile.getExtraSettingsJson()));
  }

  public static InterviewProfileView toView(InterviewProfile profile) {
    return new InterviewProfileView(
        profile.getId(),
        profile.getName(),
        profile.getDescription(),
        profile.getCompany(),
        profile.getColor(),
        profile.getIcon(),
        profile.getBehavioralQuestionCount(),
        profile.getAlgorithmQuestionCount(),
        profile.getSystemDesignQuestionCount(),
        profile.getMaxDurationMinutes(),
        profile.getTargetDifficulties().stream()
            .sorted(Comparator.comparing(Enum::name))
            .toList(),
        profile.getProblems().stream().map(Problem::getId).sorted().toList(),
        profile.getBehavioralQuestions().stream()
            .map(BehavioralQuestion::getId)
            .sorted()
            .toList(),
        profile.getSystemDesignProblems().stream()
            .map(SystemDesignProblem::getId)
            .sorted()
            .toList(),
        profile.getProblems().size(),
        profile.getBehavioralQuestions().size(),
        profile.getSystemDesignProblems().size(),
        profile.isArchived(),
        profile.getExtraSettingsJson(),
        profile.getCreatedAt(),
        profile.getUpdatedAt());
  }

  public static InterviewProfileSummaryView toSummary(InterviewProfile profile) {
    return new InterviewProfileSummaryView(
        profile.getId(),
        profile.getName(),
        profile.getDescription(),
        profile.getCompany(),
        profile.getColor(),
        profile.getIcon(),
        profile.getBehavioralQuestionCount(),
        profile.getAlgorithmQuestionCount(),
        profile.getSystemDesignQuestionCount(),
        profile.getMaxDurationMinutes(),
        profile.getProblems().size(),
        profile.getBehavioralQuestions().size(),
        profile.getSystemDesignProblems().size(),
        profile.isArchived(),
        profile.getUpdatedAt());
  }

  private static void applyFields(
      InterviewProfile profile,
      InterviewProfileForm form,
      Set<Problem> problems,
      Set<BehavioralQuestion> behavioralQuestions,
      Set<SystemDesignProblem> systemDesignProblems) {
    profile.setName(form.name().trim());
    profile.setDescription(normalize(form.description()));
    profile.setCompany(normalize(form.company()));
    profile.setColor(form.color().trim());
    profile.setIcon(form.icon().trim());
    profile.setBehavioralQuestionCount(form.behavioralQuestionCount());
    profile.setAlgorithmQuestionCount(form.algorithmQuestionCount());
    profile.setSystemDesignQuestionCount(form.systemDesignQuestionCount());
    profile.setMaxDurationMinutes(form.maxDurationMinutes());
    profile.setArchived(form.archived());
    profile.setExtraSettingsJson(normalize(form.extraSettingsJson()));
    profile.setTargetDifficulties(
        form.targetDifficulties().isEmpty()
            ? EnumSet.noneOf(Difficulty.class)
            : EnumSet.copyOf(form.targetDifficulties()));
    profile.setProblems(problems);
    profile.setBehavioralQuestions(behavioralQuestions);
    profile.setSystemDesignProblems(systemDesignProblems);
  }

  private static String normalize(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}
