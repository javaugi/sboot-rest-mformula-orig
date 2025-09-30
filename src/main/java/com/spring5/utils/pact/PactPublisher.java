/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.pact;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PactPublisher {

    @Value("${pact.broker.url}")
    private String brokerUrl;

    @Value("${pact.broker.auth.username}")
    private String username;

    @Value("${pact.broker.auth.password}")
    private String password;

    @EventListener(ApplicationReadyEvent.class)
    public void publishPacts() {
        // This is typically done via Maven/Gradle plugin, not in code
        // But you can use PactPublisher if needed
        // PactPublisher.publish("target/pacts");
        /*
    PactBrokerResultPublisher.publishPacts(
        brokerUrl,
        username,
        password,
        "target/pacts"
    );
    // */
    }
}
