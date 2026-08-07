package com.projects.ring;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.projects.hashing.HashFunction;
import com.projects.node.PhysicalNode;

public class ClusterManager {
    private final HashRing ring;
    private final Set<PhysicalNode> nodes = new HashSet<>();
    private final Rebalancer rebalancer;

    // Constant
    private final int virtualNodes;
    private final int replicationFactor;

    public ClusterManager(HashFunction function, int virtualNodes, int replicationFactor) {
        this.ring = new HashRing(function);
        this.rebalancer = new Rebalancer();
        this.virtualNodes = virtualNodes;
        this.replicationFactor = replicationFactor;
    }
    
    public Optional<String> get(String key) {
        List<PhysicalNode> replicas = ring.findReplicas(key, replicationFactor);

        // Read from the first available replica
    
        for(var node : replicas) {
            Optional<String> value = node.get(key);

            if(value.isPresent()) return value;
        }

        return Optional.empty();
    }

    public void put(String key, String value) {
        List<PhysicalNode> replicas = ring.findReplicas(key, replicationFactor);
        
        // ASYNC REPLICATION
        for(var node : replicas) {
            node.put(key, value);
        }
    }

    public void delete(String key) {
        List<PhysicalNode> replicas = ring.findReplicas(key, replicationFactor);
        
        // ASYNC REPLICATION
        for(var node : replicas) {
            node.delete(key);
        }
    }

    public void addNode(PhysicalNode node) {
        nodes.add(node);

        ring.addNode(node, virtualNodes);

        rebalancer.rebalanceAfterJoin(ring, nodes, replicationFactor);
    }

    public void deleteNode(PhysicalNode leavingNode) {
        ring.removeNode(leavingNode);

        rebalancer.rebalanceBeforeLeave(leavingNode, ring, nodes, replicationFactor);

        nodes.remove(leavingNode);
    }

    public void printRing() {
        for(PhysicalNode node : nodes) {
            System.out.println(node.getHost());
            System.out.println("-----------------------------");
            node.showContent();
            System.out.println("-----------------------------");
        }
    }
}