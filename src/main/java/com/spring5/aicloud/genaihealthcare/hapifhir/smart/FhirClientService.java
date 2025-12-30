/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.apache.ApacheRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FhirClientService {

    @Value("${smart-on-fhir.fhir-server-url}")
    private String fhirServerUrl;

    public IGenericClient createFhirClient(String accessToken) {
        // Create FHIR context
        FhirContext fhirContext = FhirContext.forR4();

        // Build HTTP client with OAuth2 token
        HttpClient httpClient = HttpClientBuilder.create().build();
        ApacheRestfulClientFactory clientFactory = new ApacheRestfulClientFactory(fhirContext);

        // Configure client with bearer token
        IGenericClient client = clientFactory.newGenericClient(fhirServerUrl);
        //client.setHttpClient(httpClient);

        // Register bearer token interceptor
        client.registerInterceptor(new IClientInterceptor() {
            @Override
            public void interceptRequest(IHttpRequest theRequest) {
                theRequest.addHeader("Authorization", "Bearer " + accessToken);
            }

            @Override
            public void interceptResponse(IHttpResponse theResponse) {
                // Handle response if needed
            }
        });

        return client;
    }
}
