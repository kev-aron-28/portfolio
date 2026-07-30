package com.projects;

import java.util.ArrayList;
import java.util.List;

import com.projects.hashing.SHA256;
import com.projects.node.NodeStatus;
import com.projects.node.PhysicalNode;
import com.projects.ring.HashRing;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SHA256 hashing = new SHA256();
        
        HashRing ring = new HashRing(hashing);

        List<PhysicalNode> nodes = new ArrayList<>();
        
        for(int i = 0; i < 4; i++) {
            nodes.add(new PhysicalNode("127.0.0." + i, 8080, NodeStatus.JOINING));
        }

        nodes.forEach(n -> ring.addNode(n, 1));

        ring.printRing();
    }
}
