/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.streaming;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/*
SSE stands for Server-Sent Events, a technology that enables servers to push real-time updates to clients over HTTP. Unlike WebSockets
    (which are bidirectional), SSE is unidirectional (server → client only) and is ideal for scenarios like live notifications, stock 
    tickers, or news feeds.

Comparison
    SSE: Unidirectional, text-based, auto-reconnects, simple API
    WebSockets: Bidirectional, binary/text, more complex, full-duplex
SSE Example in Java (Spring Boot)
    Server-Side (Controller)
WebSocket Example in Java (Spring Boot)

When to Use Each
    1. Use SSE when:
        You only need server → client updates (notifications, feeds)
        You want simplicity and automatic reconnection
        You're dealing with HTTP-only environments
    2. Use WebSockets when:
        You need bidirectional communication (chat, collaboration)
        You need binary data transfer
        You require low-latency, full-duplex communication
 */

public class SimpleSseServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/events", new SseHandler());
        server.start();
        System.out.println("SSE Server started on port 8080");
    }

    static class SseHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
            exchange.getResponseHeaders().set("Connection", "keep-alive");
            exchange.sendResponseHeaders(200, 0);

            OutputStream os = exchange.getResponseBody();
            for (int i = 0; i < 5; i++) {
                String event = "data: Message " + i + "\n\n";
                os.write(event.getBytes(StandardCharsets.UTF_8));
                os.flush();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
            os.close();
        }
    }
}
