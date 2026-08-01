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
import com.projects.exceptions.InsufficientPhysicalNodes;
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

            BigInteger hash = this.hashFunction.hash(key);

            ring.put(hash, new VirtualNode(hash, key, node));
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

    public void printRing() {
        for(Map.Entry<BigInteger, VirtualNode> node : ring.entrySet()) {
            System.out.println(node.getKey() + " " + node.getValue().getOwner().getHost());
            System.out.println("|");
        }
    }

    public int virtualNodeCount() {
        return ring.size();
    }

    public int physicalNodeCount() {
        return (int) ring.values().stream()
            .map(v -> v.getOwner())
            .distinct()
            .count();
    }

    public PhysicalNode findOwner(String key) {
        if(ring.isEmpty()) {
            throw new EmptyHashRingException();
        }

        BigInteger hash = hashFunction.hash(key);

        Map.Entry<BigInteger, VirtualNode> entry = ring.ceilingEntry(hash);

        if(entry == null) {
            entry = ring.firstEntry();
        }

        return entry.getValue().getOwner();
    }

    public List<PhysicalNode> findReplicas(String key, int replicaFactor) {
        if(ring.isEmpty()) {
            throw new EmptyHashRingException();
        }

        int availableNodes = this.physicalNodeCount();

        if(replicaFactor > availableNodes) {
            throw new InsufficientPhysicalNodes(replicaFactor, availableNodes);
        }

        BigInteger hash = hashFunction.hash(key);

        List<PhysicalNode> replicas = new ArrayList<>();

        Set<UUID> visited = new HashSet<>();

        Map.Entry<BigInteger, VirtualNode> current = ring.ceilingEntry(hash);

        int inspected = 0;
        while (replicas.size() < replicaFactor && inspected < ring.size()) { 
            PhysicalNode node = current.getValue().getOwner();

            if(visited.add(node.getId())) {
                replicas.add(node);
            }

            current = ring.higherEntry(current.getKey());

            if(current == null) {
                current = ring.firstEntry();
            }
        }

        return replicas;
    }
}
