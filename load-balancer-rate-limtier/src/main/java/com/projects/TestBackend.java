package com.projects;

import java.io.IOException;

import com.projects.http.HttpServer;
import com.projects.http.ServerResponse;

public class TestBackend {
    public static void main(String[] args) throws IOException{
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        HttpServer server = new HttpServer(port, 5);

        server.get("/hi", (request) -> {
            return ServerResponse.ok("Hello");
        });

        server.start();
    }
}
