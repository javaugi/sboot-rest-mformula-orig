/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security;

import com.spring5.AiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author javaugi
 */
@Service
public class SeecureOpenAIService {

    @Value("${application.openai.url}")
    private String apiUrl;

    @Value("${application.openai.key}")
    private String apiKey;

    @Value("${openai.api.model}")
    private String modelVersion;

    @Autowired
    private @Qualifier(AiConfig.REST_TEMPLATE)
    RestTemplate restTemplate;

    /**
     * @param prompt - the question you are expecting to ask ChatGPT
     * @return the response in JSON format
     */
    public String ask(String prompt) {
        HttpEntity<String> entity
                = new HttpEntity<>(buildMessageBody(modelVersion, prompt), buildOpenAIHeaders());

        return restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class).getBody();
    }

    private HttpHeaders buildOpenAIHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private String buildMessageBody(String modelVersion, String prompt) {
        return String.format(
                "{ \"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
                modelVersion, prompt);
    }
}

/*
In the HTTP header Authorization: Bearer <apiKey>, the term "Bearer" is a type of authentication scheme that indicates the following
    token (usually an API key, JWT, or OAuth2 access token) should be used to authorize access to a protected resource. Here's a detailed breakdown:

1. What "Bearer" Means
    Bearer Authentication is defined in RFC 6750 (OAuth2.0 framework).
        It signals that the client (e.g., your frontend or backend) must "bear" (carry) the provided token to access the resource.
        The server validates the token to grant/deny access.

2. Structure of the Header
    http
    Authorization: Bearer <token>
    Bearer: The authentication scheme (fixed keyword).
    <token>: Typically:
        An API key (e.g., sk_test_123abc).
        A JWT (e.g., eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...).
        An OAuth2 access token (e.g., gho_16C7e42F... for GitHub API).

3. How It Works
    Client includes the header in a request:
    // Example in JavaScript
    fetch("/api/data", {
      headers: {
        "Authorization": `Bearer ${apiKey}`
      }
    });

Server validates the token:
    // Spring Security example
    @GetMapping("/secure")
    public String secureEndpoint(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extract token
            // Validate token (e.g., JWT or check against DB)
        }
    }
4. Why Use "Bearer"?
    Standardization: Follows OAuth2.0 conventions.
    Flexibility: Works with various token types (JWT, opaque tokens, API keys).
    Security: Tokens can be short-lived (unlike Basic Auth, which uses persistent credentials).

5. Alternatives to Bearer Tokens
    Scheme      Example                         Use Case
    Basic       Authorization: Basic <base64>	Legacy systems (username:password)
    Digest      Authorization: Digest ...       Rare (obsolete)
    API Key     X-API-Key: <key>                Simpler APIs (non-standard)
6. Security Best Practices
    Always use HTTPS (Bearer tokens are plaintext).
    Short-lived tokens: Prefer JWTs with expiry over long-lived API keys.

Storage:
    Frontend: Store in memory (not localStorage).
    Backend: Use secure environment variables.

7. Common Mistakes
    Missing the Bearer prefix:
    ❌ Authorization: eyJhbGciOi...
    ✅ Authorization: Bearer eyJhbGciOi...
        Exposing tokens in logs/URLs.
        Using weak tokens (e.g., guessable API keys).

Example in Spring Boot

// Sending a request with Bearer token
restTemplate.exchange(
    "/api/data",
    HttpMethod.GET,
    new HttpEntity<>(createHeadersWithBearer(apiKey)),
    String.class
);

private HttpHeaders createHeadersWithBearer(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token); // <- Critical "Bearer" prefix
    return headers;
}

Key Takeaways
    Bearer is a standard way to transmit tokens in HTTP headers.
    It precedes the actual token (JWT, API key, etc.).
    Always combine with HTTPS and proper token validation.
    For JWT-specific flows, you’d typically validate the token’s signature and claims on the server. Would you like an example of JWT validation in Spring Security?
 */
