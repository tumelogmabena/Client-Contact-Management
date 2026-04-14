package org.example.server;

import com.sun.net.httpserver.HttpServer;
import org.example.api.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // register your API endpoints
        server.createContext("/api/users", new UserHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port 8080");
        System.out.println("API available at http://localhost:8080/api/users");
    }
}