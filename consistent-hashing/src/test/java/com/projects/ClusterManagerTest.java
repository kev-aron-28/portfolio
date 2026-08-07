package com.projects;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.exceptions.InsufficientPhysicalNodes;
import com.projects.hashing.HashFunction;
import com.projects.hashing.SHA256;
import com.projects.node.NodeStatus;
import com.projects.node.PhysicalNode;
import com.projects.ring.ClusterManager;

public class ClusterManagerTest {
    private ClusterManager manager;
    private HashFunction function;

    @BeforeEach
    public void beforeEach() {
        function = new SHA256();
        manager = new ClusterManager(function, 2, 2); 
    }

    @Test
    public void shouldAddNode() {
        PhysicalNode node = new PhysicalNode("hostname", 80, NodeStatus.JOINING);

        manager.addNode(node);

        assertEquals(1, manager.physicalNodeCount());
    }

    @Test
    public void shouldRemoveNode() {
        PhysicalNode node = new PhysicalNode("hostname", 80, NodeStatus.JOINING);

        manager.addNode(node);
        manager.deleteNode(node);

        assertEquals(0, manager.physicalNodeCount());
    }

    @Test
    public void shouldThrowIfInsufficientNodes() {
        assertThrows(InsufficientPhysicalNodes.class, () -> {
            manager.put("key", "value");
        });
    }

    @Test
    public void shouldReturnValueInGetWhenAvailable() {
        PhysicalNode node = new PhysicalNode("hostname", 80, NodeStatus.JOINING);
        PhysicalNode node2 = new PhysicalNode("hostname2", 80, NodeStatus.JOINING);
        PhysicalNode node3 = new PhysicalNode("hostname2", 80, NodeStatus.JOINING);

        manager.addNode(node);
        manager.addNode(node2);
        manager.addNode(node3);

        manager.put("key", "value");

        Optional<String> value = manager.get("key");

        assertTrue(value.isPresent());
    }

    @Test
    public void shouldReturnEmptyWhenNoValueAvailable() {
        PhysicalNode node = new PhysicalNode("hostname", 80, NodeStatus.JOINING);
        PhysicalNode node2 = new PhysicalNode("hostname2", 80, NodeStatus.JOINING);
        PhysicalNode node3 = new PhysicalNode("hostname2", 80, NodeStatus.JOINING);

        manager.addNode(node);
        manager.addNode(node2);
        manager.addNode(node3);

        manager.put("key", "value");

        Optional<String> value = manager.get("no-present");

        assertFalse(value.isPresent());
    }
}
