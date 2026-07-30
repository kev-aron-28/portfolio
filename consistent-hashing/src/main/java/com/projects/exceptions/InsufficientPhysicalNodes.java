package com.projects.exceptions;

public class InsufficientPhysicalNodes extends RuntimeException {
    public InsufficientPhysicalNodes(int replicationFactor, int availableNodes) {
        super("Replciation factor (%d) is greater than available nodes (%d)"
            .formatted(replicationFactor, availableNodes)
        );
    }
}
