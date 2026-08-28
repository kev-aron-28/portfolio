package com.projects.balancer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.projects.http.HttpResponseWriter;
import com.projects.http.HttpServerRequestParser;
import com.projects.http.HttpServerRequestWriter;
import com.projects.http.HttpServerResponseParser;
import com.projects.http.ServerRequest;
import com.projects.http.ServerResponse;

public class LoadBalancer {
    private final ExecutorService pool;
    private final int port;
    private final List<BackendServer> backends = new ArrayList<>();
    private final LoadBalancingStrategy loadBalancingStrategy;

    public LoadBalancer(int port, int poolSize, LoadBalancingStrategy strategy) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.loadBalancingStrategy = strategy;
    }

    public void registerBackend(String host, int port) {
        backends.add(new BackendServer(host, port));
    }

    public void start() throws IOException {
        System.out.println("Load balancer listening on port: " + this.port);
        try (ServerSocket server = new ServerSocket(this.port)){
            while(true) {
                Socket socket = server.accept();

                pool.submit(() -> handleConnection(socket));
            }
        } finally {
            pool.shutdown();
        }
    }

    public void handleConnection(Socket clientSocket) {
        try (clientSocket){
            ServerRequest request = HttpServerRequestParser.parse(clientSocket);

            BackendServer backend = this.loadBalancingStrategy.select(backends);

            try (
                Socket backendSocket = new Socket(
                    backend.getHost(),
                    backend.getPort()
                )
            ) {
                HttpServerRequestWriter.write(backendSocket, request);
                
                ServerResponse response = HttpServerResponseParser.parse(backendSocket);

                System.out.println(response);

                HttpResponseWriter.write(clientSocket, response);
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
