/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class RedisBackedIdempotencyFilter extends OncePerRequestFilter {

	private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

	private final RedisBackedIdempotencyService idempotencyService;

	public RedisBackedIdempotencyFilter(RedisBackedIdempotencyService idempotencyService) {
		this.idempotencyService = idempotencyService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {

			String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);

			if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
				if (idempotencyService.isDuplicate(idempotencyKey)) {
					Object cachedResponse = idempotencyService.getResponse(idempotencyKey);
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write(cachedResponse.toString());
					return;
				}

				ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
				try {
					filterChain.doFilter(request, responseWrapper);

					if (responseWrapper.getStatus() >= 200 && responseWrapper.getStatus() < 300) {
						String responseBody = responseWrapper.toString();
						idempotencyService.storeResponse(idempotencyKey, responseBody);
					}

					responseWrapper.copyBodyToResponse();
				}
				finally {
					responseWrapper.copyBodyToResponse();
				}
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

}
