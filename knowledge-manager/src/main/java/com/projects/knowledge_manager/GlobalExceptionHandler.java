package com.projects.knowledge_manager;

import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionNotFoundException;
import com.projects.knowledge_manager.problem.service.ProblemNotFoundException;
import com.projects.knowledge_manager.tag.service.DuplicateTagNameException;
import com.projects.knowledge_manager.tag.service.TagNotFoundException;
import com.projects.knowledge_manager.topic.service.DuplicateTopicNameException;
import com.projects.knowledge_manager.topic.service.TopicNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    TopicNotFoundException.class,
    TagNotFoundException.class,
    ProblemNotFoundException.class,
    BehavioralQuestionNotFoundException.class
  })
  public String handleNotFound(RuntimeException exception, Model model) {
    model.addAttribute("errorMessage", exception.getMessage());
    return "error/not-found";
  }

  @ExceptionHandler({DuplicateTopicNameException.class, DuplicateTagNameException.class})
  public String handleConflict(RuntimeException exception, Model model) {
    model.addAttribute("errorMessage", exception.getMessage());
    return "error/conflict";
  }
}
