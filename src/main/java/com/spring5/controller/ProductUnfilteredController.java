package com.spring5.controller;

import com.spring5.entity.Product;
import com.spring5.service.ProductService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
// @RequestMapping(value = "api/inventory/product")
public class ProductUnfilteredController {

	private final ProductService productService;

	// to display all products at localhost:8080
	// to see database values at localhost:8080/h2-console
	@GetMapping
	public ResponseEntity<Collection<Product>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllUnfilteredProduct());
	}

}
