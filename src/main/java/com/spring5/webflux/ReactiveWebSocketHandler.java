/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.webflux;

// import static org.apache.spark.ml.r.RWrappers.session;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		Flux<String> outputMessages = Flux.just("Hello", "World", "!");
		Flux<WebSocketMessage> messageFlux = outputMessages.map(session::textMessage);
		return session.send(messageFlux).then(session.receive().map(WebSocketMessage::getPayloadAsText).log().then());
	}

}
