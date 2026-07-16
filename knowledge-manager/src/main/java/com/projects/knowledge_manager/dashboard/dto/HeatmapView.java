package com.projects.knowledge_manager.dashboard.dto;

import java.util.List;

public record HeatmapView(List<List<HeatmapCellView>> weeks) {}
