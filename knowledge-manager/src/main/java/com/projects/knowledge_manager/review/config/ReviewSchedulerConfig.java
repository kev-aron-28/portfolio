package com.projects.knowledge_manager.review.config;

import com.projects.knowledge_manager.review.scheduler.FixedIntervalReviewScheduler;
import com.projects.knowledge_manager.review.scheduler.ReviewScheduler;
import com.projects.knowledge_manager.review.scheduler.Sm2ReviewScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewSchedulerConfig {

  @Bean
  @ConditionalOnProperty(name = "app.review.scheduler", havingValue = "sm2", matchIfMissing = true)
  ReviewScheduler sm2ReviewScheduler() {
    return new Sm2ReviewScheduler();
  }

  @Bean
  @ConditionalOnProperty(name = "app.review.scheduler", havingValue = "fixed-interval")
  ReviewScheduler fixedIntervalReviewScheduler() {
    return new FixedIntervalReviewScheduler();
  }
}
