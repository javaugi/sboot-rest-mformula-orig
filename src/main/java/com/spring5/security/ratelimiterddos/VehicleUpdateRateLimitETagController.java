/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import com.mongodb.bulk.UpdateRequest;
import com.mongodb.operation.UpdateOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
These implementations provide:
    Rate limiting to protect against DDoS by limiting requests per API key/IP
    Optimistic concurrency control using ETags to prevent update conflicts
    Efficient caching with 304 Not Modified responses
    Vehicle-specific protection important for OTA systems
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehicles/{vin}/updates")
public class VehicleUpdateRateLimitETagController {

	private final RateLimiterService rateLimiterService;

	private final VehicleUpdateService updateService;

	@PostMapping
	public ResponseEntity<?> initiateUpdate(@PathVariable String vin, @RequestHeader("X-API-KEY") String apiKey,
			@RequestBody UpdateRequest request) {

		// Rate limiting check
		if (!rateLimiterService.tryConsume(apiKey, 1)) {
			return ResponseEntity.status(429).body("{\"error\": \"Too many requests\"}");
		}

		// Process update initiation
		UpdateOperation operation = updateService.initiateUpdate(vin, request);

		// Return with ETag for future status updates
		String etag = calculateETag(operation);

		return ResponseEntity.accepted().eTag(etag).body(operation);
	}

	@GetMapping("/{operationId}")
	public ResponseEntity<?> getUpdateStatus(@PathVariable String vin, @PathVariable String operationId,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {

		UpdateOperation operation = updateService.getOperationStatus(operationId);
		String currentETag = calculateETag(operation);

		// If client has current version, return 304 Not Modified
		if (ifNoneMatch != null && ifNoneMatch.equals(currentETag)) {
			return ResponseEntity.status(304).build();
		}

		return ResponseEntity.ok().eTag(currentETag).body(operation);
	}

	private String calculateETag(UpdateOperation operation) {
		// Create a hash of the important fields that determine consistency
		if (operation == null) {
			return "";
		}
		// return "\"" + DigestUtils.md5DigestAsHex((operation.getId() + ":" +
		// operation.getVersion()).getBytes()) + "\"";;
		return "\"" + DigestUtils.md5DigestAsHex(operation.toString().getBytes()) + "\"";
	}

}
