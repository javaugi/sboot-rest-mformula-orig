/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class AuthInterceptor implements ClientHttpRequestInterceptor {

	private static final String apiKey = "apiKey";

	private static final String apiSecret = "apiSecret";

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		// Add Verisk-specific authentication headers
		request.getHeaders().add("X-API-Key", apiKey);
		request.getHeaders().add("X-API-Secret", apiSecret);
		request.getHeaders().add("Timestamp", String.valueOf(System.currentTimeMillis()));

		return execution.execute(request, body);
	}

}
