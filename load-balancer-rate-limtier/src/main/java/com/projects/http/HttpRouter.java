package com.projects.http;

import java.util.ArrayList;
import java.util.List;

public class HttpRouter {
    private List<ServerRoute> routes = new ArrayList<>();

    public void register(HttpMethod method, String path, HttpHandler handler) {
        routes.add(new ServerRoute(method, path, handler));
    }

    public HttpHandler find(HttpMethod method, String path) {
        return this.routes
            .stream()
            .filter(r -> r.getMethod() == method && r.getPath().equals(path))
            .map(r -> r.getHandler())
            .findFirst()
            .orElse(null);
    }
}
