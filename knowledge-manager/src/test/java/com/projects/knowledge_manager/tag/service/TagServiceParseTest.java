package com.projects.knowledge_manager.tag.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class TagServiceParseTest {

  @Test
  void parsesCommaAndNewlineSeparatedNames() {
    List<String> names = TagService.parseTagNames("BFS, DFS\nSliding Window, bfs");

    assertThat(names).containsExactly("BFS", "DFS", "Sliding Window");
  }
}
