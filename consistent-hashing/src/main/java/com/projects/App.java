package com.projects;

import java.util.ArrayList;
import java.util.List;

import com.projects.hashing.SHA256;
import com.projects.node.NodeStatus;
import com.projects.node.PhysicalNode;
import com.projects.ring.ClusterManager;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SHA256 hashing = new SHA256();
        
        ClusterManager manager = new ClusterManager(hashing, 4, 2);

        List<PhysicalNode> nodes = new ArrayList<>();

        for(int i = 0; i < 4; i++) {
            nodes.add(new PhysicalNode("localhost " + i, 8080, NodeStatus.JOINING));
        }

        nodes.forEach(n -> manager.addNode(n));

        manager.put("kevin", "aron");
        manager.put("aron","tapia");

        manager.printRing();

        System.out.println("GET VALUE: " + manager.get("kevin"));

    }
}
