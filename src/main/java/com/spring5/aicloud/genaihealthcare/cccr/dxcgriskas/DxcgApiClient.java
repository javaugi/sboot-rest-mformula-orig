/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// 1. DxCG's Primary Integration Methods
// A. SOAP Web Services (Most Common)
// B. REST API with Custom Authentication
@Service
public class DxcgApiClient {

	private final RestTemplate restTemplate;

	private final String baseUrl = "https://api.verisk.com/dxcg/v1";

	public DxcgApiClient(@Value("${verisk.api.key}") String apiKey) {
		this.restTemplate = createRestTemplate(apiKey);
	}

	private RestTemplate createRestTemplate(String apiKey) {
		return new RestTemplateBuilder().rootUri(baseUrl)
			.defaultHeader("X-API-Key", apiKey)
			.defaultHeader("Content-Type", "application/json")
			.connectTimeout(Duration.ofSeconds(30))
			.readTimeout(Duration.ofSeconds(60))
			.build();
	}

}
