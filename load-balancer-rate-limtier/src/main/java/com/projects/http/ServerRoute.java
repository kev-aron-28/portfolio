package com.projects.http;

public class ServerRoute {
    private final HttpMethod method;
    private final String path;
    private final HttpHandler handler;

    public ServerRoute(HttpMethod method, String path, HttpHandler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
