/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

	@Autowired
	private ProductRepository productRepository;

	// @Cacheable annotation abstracts the cache-aside pattern.
	// Spring handles the cache lookup and population on a miss.
	@Cacheable(value = "products", key = "#productId")
	public Product getProduct(String productId) {
		// This method body is only executed if the cache miss occurs.
		Long id = Long.valueOf(productId);
		return productRepository.findById(id).orElse(Product.builder().build());
		// .orElseThrow(() -> new ProductNotFoundException(productId));
	}

	// @CacheEvict ensures the cache is cleared on an update.
	@CacheEvict(value = "products", key = "#product.id")
	public Product updateProduct(Product product) {
		return productRepository.save(product);
	}

}
