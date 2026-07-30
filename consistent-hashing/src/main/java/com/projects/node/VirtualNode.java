package com.projects.node;

import java.math.BigInteger;

public class VirtualNode {
    private final BigInteger id;
    private final String key;
    private final PhysicalNode owner;

    public VirtualNode(BigInteger id, String key, PhysicalNode owner) {
        this.id = id;
        this.key = key;
        this.owner = owner;
    }

    public BigInteger getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public PhysicalNode getOwner() {
        return owner;
    }   
}
