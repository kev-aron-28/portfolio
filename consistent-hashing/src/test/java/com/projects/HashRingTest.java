package com.projects;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.exceptions.EmptyHashRingException;
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

        assertEquals(1, ring.virtualNodeCount());
    }

    @Test
    public void shouldRemoveNode() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 1);

        ring.removeNode(node);

        assertEquals(0, ring.virtualNodeCount());
    }

    @Test
    public void shouldLocate() {
        PhysicalNode node = new PhysicalNode("host", 80, NodeStatus.JOINING);

        ring.addNode(node, 1);

        BigInteger token = ring.hash("key");

        assertNotNull(ring.locate(token));
    }

    @Test
    public void shouldThrowIfRingIsEmpty() {
        BigInteger token = ring.hash("key");

        assertThrows(EmptyHashRingException.class, () -> {
            ring.locate(token);
        });
    }
}
