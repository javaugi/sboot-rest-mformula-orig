/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.webflux;

import java.net.URI;
import java.time.Duration;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

public class ReactiveJavaClientWebSocket {

	public static void main(String[] args) throws InterruptedException {

		WebSocketClient client = new ReactorNettyWebSocketClient();
		client
			.execute(URI.create("ws://localhost:8080/event-emitter"),
					session -> session.send(Mono.just(session.textMessage("event-spring-reactive-client-websocket")))
						.thenMany(session.receive().map(WebSocketMessage::getPayloadAsText).log())
						.then())
			.block(Duration.ofSeconds(10L));
	}

}
