/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Product updateProduct(Long id, Product updated) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(updated.getName());
        product.setPrice(updated.getPrice());
        productRepository.save(product);

        // Proactive Cache Trigger
        eventPublisher.publishEvent(new ProductCacheUpdateEvent(this, product.getId()));
        return product;
    }
}
