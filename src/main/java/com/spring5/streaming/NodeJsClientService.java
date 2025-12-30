/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.streaming;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NodeJsClientService {

    private final WebClient webClient;
    private final RestTemplate restTemplate;

    // WebClient is preferred in modern Spring Boot
    public NodeJsClientService(WebClient.Builder webClientBuilder, RestTemplate restTemplate) {
        this.webClient = webClientBuilder.baseUrl("http://nodejs-service:3000").build();
        this.restTemplate = restTemplate;
    }

    public String callNodeJsApi(String data) {
        return webClient.post()
                .uri("/api/process-data")
                .bodyValue(data) // Pass data as request body
                .retrieve()
                .bodyToMono(String.class) // Expect a String response
                .block(); // Use block() for simplicity in a service, or switch to Reactor/WebFlux for full async
    }
}
