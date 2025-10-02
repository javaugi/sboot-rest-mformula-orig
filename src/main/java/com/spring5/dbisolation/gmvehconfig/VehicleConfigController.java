/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleConfigController {

	@GetMapping("/{id}/configurations")
	@CircuitBreaker(name = "configService", fallbackMethod = "getCachedConfig")
	@RateLimiter(name = "configService")
	@Retry(name = "configService")
	public ResponseEntity<VehicleConfig> getConfiguration(@PathVariable String id,
			@RequestParam(required = false) String trim) {
		// Implementation

		return ResponseEntity.ok(VehicleConfig.builder().id(1L).build());
	}

	public ResponseEntity<VehicleConfig> getCachedConfig(String id, String trim, Exception e) {
		// Fallback to cache
		return ResponseEntity.ok(VehicleConfig.builder().id(1L).build());
	}

}
