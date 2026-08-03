package com.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        
        ClusterManager manager = new ClusterManager(hashing);

        List<PhysicalNode> nodes = new ArrayList<>();

        for(int i = 0; i < 4; i++) {
            nodes.add(new PhysicalNode("localhost " + i, 8080, NodeStatus.JOINING));
        }

        nodes.forEach(n -> manager.addNode(n));

        boolean stop = false;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Enter key:value [STOP to end program]");
            String entry = scanner.nextLine();

            if(entry.equals("STOP")) {
                break;
            }

            String parts[] = entry.split(":");
            manager.put(parts[0], parts[1]);

            manager.printRing();
        } while (!stop);   
    }
}
