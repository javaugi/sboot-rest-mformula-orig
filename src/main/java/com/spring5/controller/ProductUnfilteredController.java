package com.spring5.controller;

import com.spring5.entity.Product;
import com.spring5.service.ProductService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping("/{id}/decrement")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> {
                    if (product.getQuantity() <= 0) {
                        return ResponseEntity.badRequest().body("Out of stock");
                    }
                    product.setQuantity(product.getQuantity() - 1);
                    productService.save(product);
                    return ResponseEntity.ok(product);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/decrement")
    public ResponseEntity<?> decrementQuantity(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> {
                    if (product.getQuantity() <= 0) {
                        return ResponseEntity.badRequest().body("Out of stock");
                    }
                    product.setQuantity(product.getQuantity() - 1);
            productService.save(product);
                    return ResponseEntity.ok(product);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // Map DELETE requests to /products/{id}
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        productService.delete(product);
        return ResponseEntity.noContent().build();
    }
}
