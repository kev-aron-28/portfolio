package com.projects.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpResponseWriter {
    public void write(Socket socket, ServerResponse response) throws IOException {
        OutputStream output = socket.getOutputStream();

        StringBuilder httpResponse = new StringBuilder();

        httpResponse
            .append("HTTP/1.1 ")
            .append(response.getStatusCode())
            .append(" ")
            .append(response.getReasonPhrase())
            .append("\r\n");
        
        response.getHeaders().forEach((name, value) -> {
            httpResponse
                .append(name)
                .append(": ")
                .append(value)
                .append("\r\n");
        });

        httpResponse.append("\r\n");

        httpResponse.append(response.getBody());

        byte[] responseBytes = httpResponse
            .toString()
            .getBytes(StandardCharsets.UTF_8);

        output.write(responseBytes);
        output.flush();
    }
}
