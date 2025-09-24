/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

//import static com.mongodb.client.model.Filters.where;
import com.spring5.entity.Product;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author javaugi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductTraditionalRestController {

    private final Map<Long, Product> productStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // POST - Create product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        long id = idGenerator.getAndIncrement();
        product.setId(id);
        productStore.put(id, product);
        return ResponseEntity.created(URI.create("/api/products/" + id)).body(product);
    }

    // PUT - Replace product
    @PutMapping("/{id}")
    public ResponseEntity<Product> replaceProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productStore.put(id, product);
        return ResponseEntity.ok(product);
    }

    // PATCH - Partial update
    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Product existing = productStore.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        if (updates.containsKey("name")) {
            existing.setName((String) updates.get("name"));
        }
        if (updates.containsKey("price")) {
            existing.setPrice(new BigDecimal(updates.get("price").toString()));
        }
        return ResponseEntity.ok(existing);
    }
}
