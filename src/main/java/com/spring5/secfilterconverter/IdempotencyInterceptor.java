/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IdempotencyInterceptor implements HandlerInterceptor {

	private final Map<String, ResponseEntity<?>> cache = new ConcurrentHashMap<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			String key = request.getHeader("Idempotency-Key");
			if (key != null && cache.containsKey(key)) {
				ResponseEntity<?> cachedResponse = cache.get(key);
				response.setStatus(cachedResponse.getStatusCodeValue());
				// Write cached body if needed...
				return false; // skip controller
			}
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			String key = request.getHeader("Idempotency-Key");
			if (key != null && !cache.containsKey(key)) {
				// Simplified: store response code only; real impl needs response wrapper
				cache.put(key, ResponseEntity.status(response.getStatus()).build());
			}
		}
	}

}
