package com.projects.http;

@FunctionalInterface
public interface HttpHandler {
    ServerResponse handle(ServerRequest request);
}
