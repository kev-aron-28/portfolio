package com.projects.ring;

import java.util.Optional;

import com.projects.node.PhysicalNode;

public class ClusterManager {
    private final HashRing ring;

    public ClusterManager(HashRing ring) {
        this.ring = ring;
    }
    
    public Optional<String> get(String key) {
        PhysicalNode owner = ring.findOwner(key);

        return owner.get(key);
    }

    public void put(String key, String value) {
        PhysicalNode owner = ring.findOwner(key);
        owner.put(key, value);
    }
}
