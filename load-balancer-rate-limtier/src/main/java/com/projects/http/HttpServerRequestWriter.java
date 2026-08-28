package com.projects.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServerRequestWriter {
    public static void write(Socket backendSocket, ServerRequest request) throws IOException {
        OutputStream output = backendSocket.getOutputStream();

        StringBuilder httpRequest = new StringBuilder();

        httpRequest
            .append(request.getMethod())
            .append(" ")
            .append(request.getPath())
            .append(" ")
            .append(request.getHttpVersion())
            .append("\r\n");
        
        request.getHeaders().forEach((name, value) -> {
            httpRequest
                .append(name)
                .append(": ")
                .append(value)
                .append("\r\n");
        });

        httpRequest.append("\r\n");

        if(request.getBody() != null) {
            httpRequest.append(request.getBody());
        }

        output.write(
            httpRequest
                .toString()
                .getBytes(StandardCharsets.UTF_8)  
        );

        output.flush();
    }
}
