/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/wfusers")
public class ReactiveUserController {

    private final static Logger log = LoggerFactory.getLogger(ReactiveUserController.class);

    /*
    @Autowired
    ReactiveUserRepository userRepository;

    @GetMapping
    public Flux<RWFUser> getAllUsers() {
        return userRepository.findAll(); // Returns Flux<User>
    }

    @GetMapping("/{id}")
    public Mono<RWFUser> getUserById(@PathVariable Long id) {
        return userRepository.findById(id); // Returns Mono<User>
    }
    // */

    public void subWebClient() {
        WebClient client = WebClient.create("https://api.example.com");

        Mono<RWFUser> user = client.get()
                .uri("/users/123")
                .retrieve()
                .bodyToMono(RWFUser.class);

        user.subscribe(u -> System.out.println(u.getName()));
    }

    /*
    public void subReq() {
        Flux.range(1, 1000) // Publisher emits 1-1000
                .onBackpressureBuffer(50) // Buffer 50 items max
                .subscribe(
                        item -> process(item), // Process items
                        err -> log.error(err), // Handle errors
                        () -> log.info("Done"),
                        subscription -> subscription.request(10) // Request 10 items initially
                );
    }
    // */
}
