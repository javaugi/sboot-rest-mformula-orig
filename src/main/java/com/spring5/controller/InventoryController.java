package com.spring5.controller;

import com.spring5.entity.Product;
import com.spring5.service.ProductService;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/inventory/product")
public class InventoryController {

	private final ProductService productService;

	/**
	 * @return all the products that are not recalled
	 */
	@GetMapping
	public ResponseEntity<Collection<Product>> getAllProducts() {
		return ResponseEntity.ok(productService.findAll());
	}

	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		return ResponseEntity.ok(productService.save(product));
	}

	@GetMapping("/{id}")
	ResponseEntity<Product> findProduct(@PathVariable Long id) {
		Optional<Product> byId = productService.findById(id);

		return byId.map(ResponseEntity::ok).orElse(null);
	}

}
