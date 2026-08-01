package com.projects;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.exceptions.EmptyHashRingException;
import com.projects.exceptions.InsufficientPhysicalNodes;
import com.projects.hashing.HashFunction;
import com.projects.hashing.SHA256;
import com.projects.node.NodeStatus;
import com.projects.node.PhysicalNode;
import com.projects.ring.HashRing;

public class HashRingTest {
    private HashRing ring;
    private HashFunction sha256;

    @BeforeEach()
    public void beforeEach() {
        sha256 = new SHA256();
        ring = new HashRing(sha256);
    }

    @Test
    public void shouldAddNode() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 1);

        assertEquals(1, ring.physicalNodeCount());
    }

    @Test
    public void shouldRemoveNode() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 1);

        ring.removeNode(node);

        assertEquals(0, ring.physicalNodeCount());
    }

    @Test
    public void shouldCreateVirtualNodes() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 2);

        assertEquals(2, ring.virtualNodeCount());
    }

    @Test
    public void shouldThrowIfRingIsEmptyWhenFindingOwner() {
        assertThrows(EmptyHashRingException.class, () -> {
            ring.findOwner("key-1");
        });
    }

    @Test
    public void shouldReturnOwner() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 2);

        assertNotNull(ring.findOwner("key-1"));
    }

    @Test
    public void shouldThrowIfInsufficientReplicas() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 2);

        assertThrows(InsufficientPhysicalNodes.class, () -> {
            ring.findReplicas("key-2", 4);
        });
    }

    @Test
    public void shouldFindReplicas() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 2);

        PhysicalNode node2 = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node2, 2);

        List<PhysicalNode> result = ring.findReplicas("key-1", 2);
        
        assertEquals(2, result.size());
    }
}
