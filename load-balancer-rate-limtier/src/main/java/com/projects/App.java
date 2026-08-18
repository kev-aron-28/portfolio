package com.projects;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.projects.http.HttpResponseWriter;
import com.projects.http.HttpServerRequestParser;
import com.projects.http.ServerRequest;
import com.projects.http.ServerResponse;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
        HttpServerRequestParser parser = new HttpServerRequestParser();
        HttpResponseWriter writer = new HttpResponseWriter();

        ServerSocket server = new ServerSocket(3000);

        System.out.println("Server listening on port 3000...");

        Socket socket = server.accept();

        ServerRequest request = parser.parse(socket);

        System.out.println(request);

        ServerResponse response = ServerResponse.ok("HI");
        writer.write(socket, response);

        socket.close();
        server.close();
    }
}
