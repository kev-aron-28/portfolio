package com.projects.knowledge_manager.learning;

import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import com.projects.knowledge_manager.review.scheduler.ReviewHistoryEntry;
import com.projects.knowledge_manager.review.scheduler.ReviewResult;
import com.projects.knowledge_manager.review.scheduler.ReviewScheduler;
import java.sql.Types;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LearningService {
  public record Tree(long id, String title) {}
  public static final int BOARD_POSITION_LIMIT = 10000;

  public record Section(long id, long treeId, Long parentId, String title, Integer boardX, Integer boardY) {}
  public record Node(long id, long treeId, Long sectionId, String title, Kind kind, String criterion,
                     String prompt, boolean unlocked, LocalDate completedOn, String evidence,
                     LocalDate nextReviewOn, Integer boardX, Integer boardY) {}
  public record Edge(long nodeId, long prerequisiteId, boolean required) {}
  public record Focus(long id, long nodeId, String title, String activity, OffsetDateTime startedAt) {}
  public record Time(long seconds, long study, long practice, long review) {}
  public record Stats(long understanding, long dexterity, long application, long mastery) {}

  public enum BoardItem {
    SECTION("""
        UPDATE learning_section SET board_x = :x, board_y = :y
        WHERE tree_id = :tree AND id = :item AND parent_id IS NOT DISTINCT FROM :board
        """),
    NODE("""
        UPDATE learning_node SET board_x = :x, board_y = :y
        WHERE tree_id = :tree AND id = :item AND section_id IS NOT DISTINCT FROM :board
        """);

    private final String updatePosition;

    BoardItem(String updatePosition) {
      this.updatePosition = updatePosition;
    }
  }

  public enum Kind {
    TOPIC("Tema", "+1 Entendimiento", "bi-book"),
    PRACTICE("Reto", "+2 Destreza", "bi-crosshair"),
    PROJECT("Proyecto", "+3 Aplicación", "bi-tools"),
    MASTERY("Reto final", "+1 Maestría", "bi-trophy");

    public final String label;

    public final String title;
    public final String reward;
    public final String icon;

    Kind(String title, String reward, String icon) {
      this.title = title;
      this.reward = reward;
      this.icon = icon;
      this.label = title + " · " + reward;
    }
  }

  public enum Activity { STUDY, PRACTICE, REVIEW }

  private final JdbcClient db;
  private final ReviewScheduler scheduler;
  private final ReviewSchedulingProperties properties;

  public LearningService(JdbcClient db, ReviewScheduler scheduler, ReviewSchedulingProperties properties) {
    this.db = db;
    this.scheduler = scheduler;
    this.properties = properties;
  }

  public LocalDate today() {
    return LocalDate.now(properties.getZoneId());
  }

  public List<Tree> trees() {
    return db.sql("SELECT * FROM learning_tree ORDER BY id").query(Tree.class).list();
  }

  public Tree tree(long id) {
    return db.sql("SELECT * FROM learning_tree WHERE id = ?").param(id).query(Tree.class)
        .optional().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Transactional
  public void deleteTree(long treeId) {
    lockTree(treeId);
    db.sql("DELETE FROM learning_edge WHERE tree_id = ?").param(treeId).update();
    db.sql("DELETE FROM learning_review WHERE node_id IN (SELECT id FROM learning_node WHERE tree_id = ?)")
        .param(treeId).update();
    db.sql("DELETE FROM learning_focus WHERE node_id IN (SELECT id FROM learning_node WHERE tree_id = ?)")
        .param(treeId).update();
    db.sql("DELETE FROM learning_node WHERE tree_id = ?").param(treeId).update();
    db.sql("DELETE FROM learning_tree WHERE id = ?").param(treeId).update();
  }

  public List<Section> sections(long treeId) {
    return db.sql("SELECT * FROM learning_section WHERE tree_id = ? ORDER BY id")
        .param(treeId).query(Section.class).list();
  }

  @Transactional
  public long saveSection(long treeId, Long sectionId, Long parentId, String title) {
    lockTree(treeId);
    String name = required(title, 120);
    if (sectionId != null) {
      int updated = db.sql("UPDATE learning_section SET title = ? WHERE tree_id = ? AND id = ?")
          .params(name, treeId, sectionId).update();
      if (updated == 0) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      return sectionId;
    }
    validateSection(treeId, parentId);
    return db.sql("""
        INSERT INTO learning_section(tree_id, parent_id, title)
        VALUES (:tree, :parent, :title) RETURNING id
        """).param("tree", treeId).param("parent", parentId, Types.BIGINT).param("title", name)
        .query(Long.class).single();
  }

  @Transactional
  public void saveBoardPosition(long treeId, Long boardId, BoardItem type, long itemId, int x, int y) {
    if (x < -BOARD_POSITION_LIMIT || x > BOARD_POSITION_LIMIT
        || y < -BOARD_POSITION_LIMIT || y > BOARD_POSITION_LIMIT) {
      throw invalid("La posición está fuera del tablero.");
    }
    lockTree(treeId);
    int updated = db.sql(type.updatePosition)
        .param("x", x).param("y", y).param("tree", treeId).param("item", itemId)
        .param("board", boardId, Types.BIGINT).update();
    if (updated == 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }

  private void validateSection(long treeId, Long sectionId) {
    if (sectionId != null && !db.sql("SELECT id FROM learning_section WHERE tree_id = ? AND id = ?")
        .params(treeId, sectionId).query(Long.class).optional().isPresent()) {
      throw invalid("La sección debe pertenecer a este árbol.");
    }
  }

  public List<Node> nodes(long treeId) {
    return db.sql("SELECT * FROM learning_node WHERE tree_id = ? ORDER BY id")
        .param(treeId).query(Node.class).list();
  }

  public List<Node> due() {
    return db.sql("SELECT * FROM learning_node WHERE next_review_on <= ? ORDER BY next_review_on, id")
        .param(today()).query(Node.class).list();
  }

  public List<Edge> edges(long treeId) {
    return db.sql("SELECT node_id, prerequisite_id, required FROM learning_edge WHERE tree_id = ?")
        .param(treeId).query(Edge.class).list();
  }

  public int points(List<Node> nodes) {
    return 2 - (int) nodes.stream().filter(node -> node.unlocked() && node.completedOn() == null).count();
  }

  public boolean ready(Node node, List<Node> nodes, List<Edge> edges) {
    return nodes.stream().filter(candidate -> candidate.completedOn() == null)
        .noneMatch(candidate ->
            (node.kind() == Kind.MASTERY && candidate.kind() != Kind.MASTERY)
            || edges.stream().anyMatch(edge -> edge.required() && edge.nodeId() == node.id()
                && edge.prerequisiteId() == candidate.id()));
  }

  public Stats stats() {
    return db.sql("""
        SELECT count(*) FILTER (WHERE kind = 'TOPIC') AS understanding,
               count(*) FILTER (WHERE kind = 'PRACTICE') * 2 AS dexterity,
               count(*) FILTER (WHERE kind = 'PROJECT') * 3 AS application,
               count(*) FILTER (WHERE kind = 'MASTERY') AS mastery
        FROM learning_node WHERE completed_on IS NOT NULL
        """).query(Stats.class).single();
  }

  public long createTree(String title) {
    return db.sql("INSERT INTO learning_tree(title) VALUES (?) RETURNING id")
        .param(required(title, 120)).query(Long.class).single();
  }

  @Transactional
  public void saveNode(long treeId, Long nodeId, Long sectionId, String title, Kind kind,
                       String criterion, String prompt, Set<Long> prerequisites, Set<Long> recommended) {
    lockTree(treeId);
    validateSection(treeId, sectionId);
    List<Node> nodes = nodes(treeId);
    if (nodeId != null) {
      kind = find(nodes, nodeId).kind();
    } else if (kind == Kind.MASTERY && nodes.stream().anyMatch(node -> node.kind() == Kind.MASTERY)) {
      throw invalid("El árbol ya tiene un reto final.");
    }
    title = required(title, 120);
    criterion = required(criterion, 4000);
    prompt = required(prompt, 4000);
    long id;
    if (nodeId == null) {
      id = db.sql("""
          INSERT INTO learning_node(tree_id, section_id, title, kind, criterion, prompt)
          VALUES (:tree, :section, :title, :kind, :criterion, :prompt) RETURNING id
          """).param("tree", treeId).param("section", sectionId, Types.BIGINT).param("title", title)
          .param("kind", kind.name()).param("criterion", criterion).param("prompt", prompt)
          .query(Long.class).single();
    } else {
      id = nodeId;
      db.sql("""
          UPDATE learning_node SET
              board_x = CASE WHEN section_id IS NOT DISTINCT FROM :section THEN board_x END,
              board_y = CASE WHEN section_id IS NOT DISTINCT FROM :section THEN board_y END,
              section_id = :section, title = :title,
              criterion = :criterion, prompt = :prompt WHERE id = :id
          """).param("section", sectionId, Types.BIGINT).param("title", title)
          .param("criterion", criterion).param("prompt", prompt).param("id", id).update();
      if (find(nodes, id).unlocked()) {
        return;
      }
    }
    db.sql("""
        DELETE FROM learning_edge WHERE node_id = ?
        """).param(id).update();
    for (long source : prerequisites) {
      saveEdge(treeId, id, find(nodes, source), true);
    }
    for (long source : recommended) {
      if (!prerequisites.contains(source)) {
        saveEdge(treeId, id, find(nodes, source), false);
      }
    }
  }

  private void saveEdge(long treeId, long id, Node source, boolean required) {
    if (source.id() >= id || source.kind() == Kind.MASTERY) {
      throw invalid("Elige un tema anterior al nodo como conexión.");
    }
    db.sql("INSERT INTO learning_edge VALUES (?, ?, ?, ?)")
        .params(treeId, id, source.id(), required).update();
  }

  @Transactional
  public void unlock(long treeId, long id) {
    lockTree(treeId);
    List<Node> nodes = nodes(treeId);
    Node node = find(nodes, id);
    if (node.unlocked()) {
      return;
    }
    if (points(nodes) <= 0 || !ready(node, nodes, edges(treeId))) {
      throw invalid("El nodo requiere puntos disponibles y sus prerrequisitos completos.");
    }
    db.sql("UPDATE learning_node SET unlocked = TRUE WHERE id = ?").param(id).update();
  }

  @Transactional
  public void complete(long treeId, long id, String evidence, boolean priorLearning) {
    lockTree(treeId);
    List<Node> nodes = nodes(treeId);
    Node node = find(nodes, id);
    if (node.completedOn() != null) {
      return;
    }
    if (!ready(node, nodes, edges(treeId)) || (!node.unlocked() && !priorLearning)) {
      throw invalid("Completa los prerrequisitos y activa el nodo.");
    }
    if (node.kind() != Kind.TOPIC || !node.unlocked()) {
      evidence = required(evidence, 4000);
    } else if (evidence.length() > 4000) {
      throw invalid("La evidencia admite hasta 4000 caracteres.");
    }
    LocalDate date = today();
    db.sql("""
        UPDATE learning_node SET unlocked = TRUE, completed_on = ?, evidence = ?, next_review_on = ?
        WHERE id = ?
        """).params(date, evidence.strip(), date.plusDays(1), id).update();
  }

  @Transactional
  public void review(long treeId, long id, LocalDate scheduledOn, int rating) {
    lockTree(treeId);
    Node node = find(nodes(treeId), id);
    if (rating != 1 && rating != 3 && rating != 5) {
      throw invalid("Selecciona un resultado de repaso.");
    }
    LocalDate date = today();
    if (!scheduledOn.equals(node.nextReviewOn()) || scheduledOn.isAfter(date)) {
      return;
    }
    LocalDate next = scheduler.calculateNextReview(new ReviewResult(date, rating), history(id));
    db.sql("INSERT INTO learning_review(node_id, reviewed_on, rating, next_review_on) VALUES (?, ?, ?, ?)")
        .params(id, date, rating, next).update();
    db.sql("UPDATE learning_node SET next_review_on = ? WHERE id = ?").params(next, id).update();
  }

  public List<ReviewHistoryEntry> history(long nodeId) {
    return db.sql("""
        SELECT reviewed_on AS review_date, rating, next_review_on AS next_review_date
        FROM learning_review WHERE node_id = ? ORDER BY id
        """).param(nodeId).query(ReviewHistoryEntry.class).list();
  }

  public Focus activeFocus() {
    return db.sql("""
        SELECT f.id, f.node_id, n.title, f.activity, f.started_at
        FROM learning_focus f JOIN learning_node n ON n.id = f.node_id
        WHERE f.ended_at IS NULL
        """).query(Focus.class).optional().orElse(null);
  }

  @Transactional
  public void startFocus(long treeId, long id, Activity activity) {
    lockTree(treeId);
    Node node = find(nodes(treeId), id);
    if (!node.unlocked() || (activity == Activity.REVIEW && node.completedOn() == null)) {
      throw invalid("Activa el nodo antes de empezar enfoque y complétalo para repasar.");
    }
    int inserted = db.sql("""
        INSERT INTO learning_focus(node_id, activity, started_at)
        VALUES (?, ?, CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING
        """).params(id, activity.name()).update();
    if (inserted == 0) {
      throw invalid("Ya hay una sesión de enfoque activa.");
    }
  }

  public void stopFocus(long id) {
    db.sql("UPDATE learning_focus SET ended_at = CURRENT_TIMESTAMP WHERE id = ? AND ended_at IS NULL")
        .param(id).update();
  }

  public Time time(Long treeId, Long nodeId) {
    String filter = "";
    List<Object> parameters = List.of();
    if (nodeId != null) {
      filter = " AND f.node_id = ?";
      parameters = List.of(nodeId);
    } else if (treeId != null) {
      filter = " AND n.tree_id = ?";
      parameters = List.of(treeId);
    }
    return time(filter, parameters);
  }

  public Time sectionTime(long sectionId) {
    return time("""
        AND n.section_id IN (
          WITH RECURSIVE subtree(id, tree_id) AS (
            SELECT id, tree_id FROM learning_section WHERE id = ?
            UNION ALL
            SELECT child.id, child.tree_id FROM learning_section child
              JOIN subtree ON child.tree_id = subtree.tree_id AND child.parent_id = subtree.id
          )
          SELECT id FROM subtree
        )
        """, List.of(sectionId));
  }

  private Time time(String filter, List<Object> parameters) {
    return db.sql("""
        SELECT coalesce(sum(seconds), 0) AS seconds,
               coalesce(sum(seconds) FILTER (WHERE activity = 'STUDY'), 0) AS study,
               coalesce(sum(seconds) FILTER (WHERE activity = 'PRACTICE'), 0) AS practice,
               coalesce(sum(seconds) FILTER (WHERE activity = 'REVIEW'), 0) AS review
        FROM (
          SELECT f.activity, extract(epoch FROM f.ended_at - f.started_at) AS seconds
          FROM learning_focus f JOIN learning_node n ON n.id = f.node_id
          WHERE f.ended_at IS NOT NULL
        """ + filter + ") sessions").params(parameters).query(Time.class).single();
  }

  public long branchTime(Set<Long> nodeIds) {
    return db.sql("""
        SELECT coalesce(sum(extract(epoch FROM ended_at - started_at)), 0)
        FROM learning_focus WHERE ended_at IS NOT NULL AND node_id IN (:nodeIds)
        """).param("nodeIds", nodeIds).query(Long.class).single();
  }

  private void lockTree(long treeId) {
    db.sql("SELECT id FROM learning_tree WHERE id = ? FOR UPDATE").param(treeId).query(Long.class)
        .optional().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  static Node find(List<Node> nodes, long id) {
    return nodes.stream().filter(node -> node.id() == id).findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  private static String required(String value, int max) {
    String cleaned = value.strip();
    if (cleaned.isEmpty() || cleaned.length() > max) {
      throw invalid("Introduce un valor de entre 1 y " + max + " caracteres.");
    }
    return cleaned;
  }

  private static ResponseStatusException invalid(String message) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }
}
