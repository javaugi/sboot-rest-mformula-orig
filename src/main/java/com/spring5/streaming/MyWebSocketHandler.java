/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.streaming;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());

        // Send welcome message
        try {
            session.sendMessage(new TextMessage("Connected to WebSocket server"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Handle incoming messages (bidirectional)
        String payload = message.getPayload();
        System.out.println("Received: " + payload);

        // Broadcast to all clients
        broadcast("Echo: " + payload);
    }

    private void broadcast(String message) {
        for (WebSocketSession s : sessions) {
            try {
                s.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to push updates to all clients
    public void sendUpdates(String update) {
        broadcast("Update: " + update);
    }
}
