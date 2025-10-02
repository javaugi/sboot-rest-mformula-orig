/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

/*
Important Considerations
    1. Storage: This example uses an in-memory map which is not suitable for production. In a real application, you should use a distributed cache or database.
    2. Expiration: You should implement expiration for idempotency keys to prevent the store from growing indefinitely.
    3. Request Matching: For a more robust solution, you might want to consider matching both the idempotency key AND the request content.
    4. Error Handling: Consider how to handle cases where the same idempotency key is used with different request bodies.
    5. HTTP Methods: While this example covers POST and PUT, you might want to include PATCH as well depending on your requirements.
This implementation ensures that multiple identical requests with the same idempotency key will produce the same result without side effects.

 */
@Component
public class IdempotencyFilter extends OncePerRequestFilter {

	private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

	private static final Map<String, Object> idempotencyStore = new ConcurrentHashMap<>();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Only check for POST and PUT requests
		if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {

			String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);

			if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
				// Check if we've seen this key before
				if (idempotencyStore.containsKey(idempotencyKey)) {
					// Return the cached response
					Object cachedResponse = idempotencyStore.get(idempotencyKey);
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write(cachedResponse.toString());
					return;
				}

				// Wrap the response to cache it
				ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

				try {
					filterChain.doFilter(request, responseWrapper);

					// Only cache successful responses (2xx)
					if (responseWrapper.getStatus() >= 200 && responseWrapper.getStatus() < 300) {
						byte[] responseBody = responseWrapper.getContentAsByteArray();
						idempotencyStore.put(idempotencyKey, new String(responseBody));
					}

					// Write the response to the actual client
					responseWrapper.copyBodyToResponse();
				}
				finally {
					responseWrapper.copyBodyToResponse();
				}
				return;
			}
		}

		// No idempotency key or not POST/PUT - proceed normally
		filterChain.doFilter(request, response);
	}

}
