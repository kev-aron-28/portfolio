package com.projects.knowledge_manager.review.config;

import com.projects.knowledge_manager.review.scheduler.FirstReviewScheduleCalculator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ReviewSchedulingProperties.class)
public class ReviewSchedulingConfig {

  @Bean
  FirstReviewScheduleCalculator firstReviewScheduleCalculator(
      ReviewSchedulingProperties properties) {
    return new FirstReviewScheduleCalculator(properties);
  }
}
