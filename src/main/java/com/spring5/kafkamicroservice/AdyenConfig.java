/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import com.adyen.enums.Environment;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "adyen")
@Data
public class AdyenConfig {

    private String apiKey;
    private String merchantAccount;
    private String environment;
    private String endpoint;

    @Bean
    public AdyenClient adyenClient() {
        this.environment
                = "test".equals(this.environment) ? Environment.TEST.toString() : Environment.LIVE.toString();
        AdyenClient client = new AdyenClient(apiKey, Environment.valueOf(environment.toUpperCase()));
        // Client client = new Client(apiKey, Environment.valueOf(environment.toUpperCase()));
        // client.setEnvironment(environment.equals("test") ?  Environment.TEST : Environment.LIVE);
        return client;
    }
}
