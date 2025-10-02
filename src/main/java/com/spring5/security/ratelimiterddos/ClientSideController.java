/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
public class ClientSideController {

	@Value("${application.base-url}")
	protected String applicationBaseUrl;

	private final WebClient webClient;

	public ClientSideController(WebClient.Builder builder) {
		this.webClient = builder.baseUrl(applicationBaseUrl).build();
	}

	// Method to make a GET request
	public Mono<String> getData() {
		return webClient.get().uri("/api/campaigns/123").retrieve().bodyToMono(String.class);
	}

	// Method to make a POST request
	public Mono<String> postData(String requestBody) {
		return webClient.post().uri("/data").bodyValue(requestBody).retrieve().bodyToMono(String.class);
	}

	private void jsExampleCall() {
		/*
		 * // First GET to retrieve the resource and ETag fetch('/api/campaigns/123')
		 * .then(response => { const etag = response.headers.get('ETag'); const campaign =
		 * response.json();
		 * 
		 * // Later, when making updates: return fetch('/api/campaigns/123', { method:
		 * 'PUT', headers: { 'Content-Type': 'application/json', 'If-Match': etag }, body:
		 * JSON.stringify(updatedCampaign) }); }) .then(response => { if (response.status
		 * === 412) { console.error('Conflict: Resource was modified by another user'); }
		 * return response.json(); });
		 */
	}

}
