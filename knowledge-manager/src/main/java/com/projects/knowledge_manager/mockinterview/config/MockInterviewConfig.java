package com.projects.knowledge_manager.mockinterview.config;

import com.projects.knowledge_manager.mockinterview.generator.InterviewGenerator;
import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockInterviewConfig {

  @Bean
  Map<InterviewFormat, InterviewGenerator> interviewGenerators(List<InterviewGenerator> generators) {
    Map<InterviewFormat, InterviewGenerator> map = new EnumMap<>(InterviewFormat.class);
    for (InterviewGenerator generator : generators) {
      map.put(generator.format(), generator);
    }
    return map;
  }
}
