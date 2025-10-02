/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import com.spring5.entity.Product;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JHCacheDefault1stLevelProductService {

	@Autowired
	private EntityManager entityManager;

	@Transactional
	public void demonstrateFirstLevelCache() {
		// First query - hits the database
		Product product1 = entityManager.find(Product.class, 1L);
		System.out.println("First query: " + product1.getName());

		// Second query - returns from first-level cache
		Product product2 = entityManager.find(Product.class, 1L);
		System.out.println("Second query: " + product2.getName());

		// Both product1 and product2 are the same instance
		System.out.println("Same instance? " + (product1 == product2)); // true
	}

}
