package com.projects.ring;

import java.util.List;
import java.util.Optional;

import com.projects.hashing.HashFunction;
import com.projects.node.PhysicalNode;

public class ClusterManager {
    private final HashRing ring;
    private int DEFAULT_VIRTUAL_NODES = 4;
    private int DEFAULT_REPLICAS = 2;

    public ClusterManager(HashFunction function) {
        this.ring = new HashRing(function);
    }

    public ClusterManager(HashFunction function, int virtualNodes, int replicas) {
        this.ring = new HashRing(function);
        this.DEFAULT_VIRTUAL_NODES = virtualNodes;
        this.DEFAULT_REPLICAS = replicas;
    }
    
    public Optional<String> get(String key) {
        PhysicalNode owner = ring.findOwner(key);

        return owner.get(key);
    }

    public void put(String key, String value) {
        List<PhysicalNode> replicas = ring.findReplicas(key, DEFAULT_REPLICAS);
        
        for(var node : replicas) {
            node.put(key, value);
        }
    }

    public void addNode(PhysicalNode node) {
        ring.addNode(node, DEFAULT_VIRTUAL_NODES);
    }

    public void deleteNode(PhysicalNode node) {
        ring.removeNode(node);
    }

    public void printRing() {
        ring.printRing();
    }
}
