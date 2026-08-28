package com.projects.http;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final HttpRouter router;
    private final ExecutorService pool;
    private final int port;
    
    public HttpServer(int port , int poolSize) {
        this.port = port;
        this.router = new HttpRouter();
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    public void post(String path, HttpHandler handler) {
        router.register(HttpMethod.POST, path, handler);
    }

    public void get(String path, HttpHandler handler) {
        router.register(HttpMethod.GET, path, handler);
    }

    public void put(String path, HttpHandler handler) {
        router.register(HttpMethod.PUT, path, handler);
    }

    public void delete(String path, HttpHandler handler) {
        router.register(HttpMethod.DELETE, path, handler);
    }

    public void patch(String path, HttpHandler handler) {
        router.register(HttpMethod.PATCH, path, handler);
    }

    public void start() throws IOException {
        System.out.println("SERVER RUNNING ON PORT " + this.port);
        try (
            ServerSocket server = new ServerSocket(this.port);
        ) {
            while(true) {
                Socket socket = server.accept();

                System.out.println("NEW REQUEST");
                pool.submit(() -> serve(socket));
            }
        } finally {
            pool.shutdown();
        }
    }
    
    private void serve(Socket socket) {
        try (socket) {
            ServerRequest request = HttpServerRequestParser.parse(socket);
            
            ServerResponse response;
            
            HttpHandler handler = this.router.find(
                    HttpMethod.valueOf(
                            request.getMethod()),
                    request.getPath()
            );
            
            if(handler == null) {
                response = ServerResponse.notFound("Method not found");
            } else {
                response = handler.handle(request);
            }
            
            HttpResponseWriter.write(socket, response);
        } catch(EOFException e) {
            System.out.println("Client disconnected.");
        } catch(SocketException e) {
            System.out.println(
                    "Socket disconnected: " + e.getMessage()
            );
        } catch(IOException e) {
            System.out.println(
                    "I/O error: " + e.getMessage()
            );
        } catch(Exception e) {
            System.out.println(
                    "Request handling error: " + e.getMessage()
            );
        }
    }
}
