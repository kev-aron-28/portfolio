package com.projects.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpResponseWriter {
    public static void write(Socket socket, ServerResponse response) throws IOException {
        OutputStream output = socket.getOutputStream();

        byte[] body = response.getBody() == null
                ? new byte[0]
                : response.getBody().getBytes(StandardCharsets.UTF_8);

        StringBuilder httpResponse = new StringBuilder();

        httpResponse
                .append("HTTP/1.1 ")
                .append(response.getStatusCode())
                .append(" ")
                .append(response.getReasonPhrase())
                .append("\r\n");

        // Headers
        response.getHeaders().forEach((name, value) -> {
            httpResponse
                    .append(name)
                    .append(": ")
                    .append(value)
                    .append("\r\n");
        });

        httpResponse
                .append("Content-Length: ")
                .append(body.length)
                .append("\r\n");

        httpResponse.append("\r\n");

        output.write(
                httpResponse
                        .toString()
                        .getBytes(StandardCharsets.UTF_8)
        );

        output.write(body);

        output.flush();
    }
}
