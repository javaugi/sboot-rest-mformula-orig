/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.entity.Product;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reactiveproduct")
public class ProductReactiveController {

	/*
	 * 4. Endpoints Summary HTTP Method URL Description
	 * 
	 * POST /api/products Create product (MVC) PUT /api/products/{id} Replace product
	 * (MVC) PATCH /api/products/{id} Partial update (MVC)
	 * 
	 * POST /reactive/products Create product (Reactive) PUT /reactive/products/{id}
	 * Replace product (Reactive) PATCH /reactive/products/{id} Partial update (Reactive)
	 * 
	 * 
	 * 1. Key Differences: @PostMapping vs @PutMapping Aspect @PostMapping
	 * (POST) @PutMapping (PUT) Intent Create a new resource (server generates the ID).
	 * Create or replace a resource at a specific ID/URI. Idempotency Not idempotent:
	 * Multiple calls → multiple resources. Idempotent: Multiple calls → same final
	 * resource state. Request URL /api/users → server assigns ID. /api/users/{id} →
	 * client specifies ID. HTTP Semantics POST /users means "add to this collection." PUT
	 * /users/123 means "store this resource at /users/123." Response 201 Created +
	 * location header with new ID. 200 OK or 204 No Content if updated successfully.
	 * Partial updates Typically not used for partial updates. Typically not used for
	 * partial updates (use PATCH).
	 * 
	 * 4. Idempotency in Practice Operation POST (/users) PUT (/users/123) First request
	 * Creates User ID 123 automatically Creates or replaces ID 123 Second request (same)
	 * Creates another new user Just replaces ID 123 → same result
	 * 
	 * This makes PUT safe for retries in distributed systems (e.g., network failures).
	 * 
	 * 5. When to Use Which Use POST: Creating new resource without a known ID. Submitting
	 * commands or actions that aren’t simple updates (e.g., "process payment").
	 * Non-idempotent operations where duplication might occur.
	 * 
	 * Use PUT: Full replacement of a known resource. Idempotent updates — clients can
	 * retry safely. Upsert semantics: create if doesn’t exist, replace if exists.
	 * 
	 * For partial updates, use:
	 * 
	 * @PatchMapping("/{id}") with JSON Merge Patch or JSON Patch.
	 * 
	 * 6. Quick Cheat Sheet POST = Create PUT = Create or Replace (idempotent) PATCH =
	 * Partial Update
	 */
	private final Map<Long, Product> productStore = new ConcurrentHashMap<>();

	private final AtomicLong idGenerator = new AtomicLong(1);

	// POST - Create product
	@PostMapping
	public Mono<ResponseEntity<Product>> createProduct(@RequestBody Mono<Product> productMono) {
		return productMono.map(product -> {
			long id = idGenerator.getAndIncrement();
			product.setId(id);
			productStore.put(id, product);
			return ResponseEntity.created(URI.create("/reactive/products/" + id)).body(product);
		});
	}

	// PUT - Replace product
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Product>> replaceProduct(@PathVariable Long id, @RequestBody Mono<Product> productMono) {
		return productMono.map(product -> {
			product.setId(id);
			productStore.put(id, product);
			return ResponseEntity.ok(product);
		});
	}

	// PATCH - Partial update
	@PatchMapping("/{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@PathVariable Long id,
			@RequestBody Mono<Map<String, Object>> updatesMono) {
		return updatesMono.map(updates -> {
			Product existing = productStore.get(id);
			if (existing == null) {
				return ResponseEntity.notFound().build();
			}
			if (updates.containsKey("name")) {
				existing.setName((String) updates.get("name"));
			}
			if (updates.containsKey("price")) {
				existing.setPrice(Double.valueOf(updates.get("price").toString()));
			}
			return ResponseEntity.ok(existing);
		});
	}

}
