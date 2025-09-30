/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.pact;

import com.spring5.entity.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserServiceConsumer {

    private final WebClient webClient;

    public UserServiceConsumer(@Value("${user-service.url}") String baseUrl) {
        this.webClient
                = WebClient.builder()
                        .baseUrl(baseUrl)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
    }

    public User getUserById(Long userId) {
        return webClient.get().uri("/api/users/{id}", userId).retrieve().bodyToMono(User.class).block();
    }

    public User createUser(User user) {
        return webClient
                .post()
                .uri("/api/users")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    public List<User> getUsersByStatus(String status) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/users").queryParam("status", status).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<User>>() {
                })
                .block();
    }
}
