package com.projects.ring;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.projects.node.PhysicalNode;

public class Rebalancer {
    public void rebalanceBeforeLeave(PhysicalNode leavingNode, HashRing ring, Set<PhysicalNode> nodes, int replicationFactor) {
        Set<Map.Entry<String, String>> records = new HashSet<>(leavingNode.records());

        for(var record : records) {
            List<PhysicalNode> replicas = ring.findReplicas(record.getKey(), replicationFactor);

            for(var replica : replicas) {
                replica.put(record.getKey(), record.getValue());
            }
            
            leavingNode.delete(record.getKey());
        }

    }

    public void rebalanceAfterJoin(HashRing ring, Set<PhysicalNode> nodes, int replicaFactor) {
        for(PhysicalNode node : nodes) {
            // Rebalance node, all the keys inside that node
            Set<Map.Entry<String, String>> records = new HashSet<>(node.records());

            for(var record : records) {
                syncRecord(record, node, ring, replicaFactor);
            }
        }
    }

    private void syncRecord(Map.Entry<String, String> record, PhysicalNode current, HashRing ring, int replicationFactor) {
        List<PhysicalNode> replicas = ring.findReplicas(record.getKey(), replicationFactor);
        
        // If this node should no longer store the record
        if(!replicas.contains(current)) {
            for(var replica : replicas) {
                replica.put(record.getKey(), record.getValue());
            }

            // Remove from the old owner
            current.delete(record.getKey());

            return;
        }

        // Ensure every replica has a copy
        for(var replica : replicas) {
            replica.put(record.getKey(), record.getValue());
        }
    }
}
