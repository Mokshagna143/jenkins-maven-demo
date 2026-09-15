package com.example;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {

        int port = 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {

            String response =
                    "=================================\n" +
                    " Jenkins Maven Demo\n" +
                    "=================================\n" +
                    "Application : Jenkins Maven CI\n" +
                    "Version     : 4.0\n" +
                    "Author      : M.Bramhaiah\n" +
                    "Status      : Running\n";

            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response.getBytes());
            }
        });

        server.start();

        System.out.println("Jenkins Maven Demo started on port " + port);
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
