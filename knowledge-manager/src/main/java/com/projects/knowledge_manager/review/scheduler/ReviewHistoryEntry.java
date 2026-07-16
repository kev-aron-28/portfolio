package com.projects.knowledge_manager.review.scheduler;

import java.time.LocalDate;
import java.util.List;

public record ReviewHistoryEntry(LocalDate reviewDate, int rating, LocalDate nextReviewDate) {}
