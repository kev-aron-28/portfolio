package com.projects.knowledge_manager;

import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionNotFoundException;
import com.projects.knowledge_manager.interviewprofile.service.DuplicateInterviewProfileNameException;
import com.projects.knowledge_manager.interviewprofile.service.InterviewProfileNotFoundException;
import com.projects.knowledge_manager.mockinterview.service.MockInterviewNotFoundException;
import com.projects.knowledge_manager.problem.service.ProblemNotFoundException;
import com.projects.knowledge_manager.systemdesign.service.SystemDesignProblemNotFoundException;
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
    BehavioralQuestionNotFoundException.class,
    SystemDesignProblemNotFoundException.class,
    InterviewProfileNotFoundException.class,
    MockInterviewNotFoundException.class
  })
  public String handleNotFound(RuntimeException exception, Model model) {
    model.addAttribute("errorMessage", exception.getMessage());
    return "error/not-found";
  }

  @ExceptionHandler({
    DuplicateTopicNameException.class,
    DuplicateTagNameException.class,
    DuplicateInterviewProfileNameException.class
  })
  public String handleConflict(RuntimeException exception, Model model) {
    model.addAttribute("errorMessage", exception.getMessage());
    return "error/conflict";
  }
}
