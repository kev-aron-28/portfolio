package com.projects.ring;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import com.projects.exceptions.EmptyHashRingException;
import com.projects.hashing.HashFunction;
import com.projects.node.PhysicalNode;
import com.projects.node.VirtualNode;

public class HashRing {
    private final TreeMap<BigInteger, VirtualNode> ring = new TreeMap<>();

    private final HashFunction hashFunction;

    public HashRing(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
    }

    public void addNode(PhysicalNode node, int virtualNodes) {
        for(int i = 0; i < virtualNodes; i++) {
            String key = node.getId() + "#" + i;

            BigInteger token = hash(key);

            ring.put(token, new VirtualNode(token, key, node));
        }
    }

    public void removeNode(PhysicalNode node) {
        ring
        .entrySet()
        .removeIf(entry -> entry
            .getValue()
            .getOwner()
            .equals(node)
        );

    }

    public int virtualNodeCount() {
        return ring.size();
    }

    // First virtual node clockwise
    public VirtualNode locate(BigInteger hash) {
        if(ring.isEmpty()) {
            throw new EmptyHashRingException();
        }

        Map.Entry<BigInteger, VirtualNode> entry = ring.ceilingEntry(hash);

        if(entry == null) {
            entry = ring.firstEntry();
        }

        return entry.getValue();
    }

    // Clockwise navigation
    public VirtualNode successor(BigInteger token) {
        if(ring.isEmpty()) {
            throw new EmptyHashRingException();
        }

        Map.Entry<BigInteger, VirtualNode> entry = ring.higherEntry(token);

        if(entry == null) {
            entry = ring.firstEntry();
        }

        return entry.getValue();
    }

    // Counter-clockwise navigation
    public VirtualNode predecessor(BigInteger token) {
        if(ring.isEmpty()) {
            throw new EmptyHashRingException();
        }

        Map.Entry<BigInteger, VirtualNode> entry = ring.lowerEntry(token);

        if(entry == null) {
            entry = ring.lastEntry();
        }

        return entry.getValue();
    }

    public PhysicalNode findOwner(String key) {
        return locate(
            hash(key)
        ).getOwner();
    }

    public List<PhysicalNode> findReplicas(String key, int replicaFactor) {
        if(ring.isEmpty()) {
            throw new EmptyHashRingException();
        }

        List<PhysicalNode> replicas = new ArrayList<>();

        Set<UUID> visited = new HashSet<>();

        VirtualNode current = locate(hash(key));

        while(replicas.size() < replicaFactor) {
            PhysicalNode owner = current.getOwner();

            if(visited.add(owner.getId())) {
                replicas.add(owner);
            }

            current = successor(current.getId());
        }

        return replicas;
    }

    public BigInteger hash(String key) {
        return this.hashFunction.hash(key);
    }
}
