/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.config;

// Configuration properties
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.api")
@Data
public class ExternalApiProperties {

	private String baseUrl;

	private int timeout = 5000;

	private int maxConnections = 100;

	private int maxPerRoute = 20;

}
