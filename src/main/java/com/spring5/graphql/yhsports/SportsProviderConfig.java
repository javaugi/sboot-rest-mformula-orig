/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SportsProviderConfig {

    @Value("${sports.provider:SIERRA}")
    private String providerFlag;

    @Bean
    public SportsDataProvider sportsDataProvider(
            SierraProvider sierraProvider,
            GraphiteProviderAdapter graphiteProviderAdapter) {

        if ("GRAPHITE".equalsIgnoreCase(providerFlag)) {
            return graphiteProviderAdapter;
        } else {
            return sierraProvider;
        }
    }
}
