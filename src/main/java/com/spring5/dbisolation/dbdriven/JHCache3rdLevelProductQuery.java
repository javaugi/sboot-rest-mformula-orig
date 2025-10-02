/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JHCache3rdLevelProductQuery {

	@Autowired
	private JHCache2ndLevelProductRepository productRepository;

	public void demonstrateQueryCache() {
		// First call - hits database
		List<JPAHibernateCache2ndLevelProduct> products1 = productRepository
			.findExpensiveProducts(new BigDecimal("100"));
		System.out.println("First call count: " + products1.size());

		// Second call with same parameters - hits query cache
		List<JPAHibernateCache2ndLevelProduct> products2 = productRepository
			.findExpensiveProducts(new BigDecimal("100"));
		System.out.println("Second call count: " + products2.size());

		// Third call with different parameters - hits database again
		List<JPAHibernateCache2ndLevelProduct> products3 = productRepository
			.findExpensiveProducts(new BigDecimal("200"));
		System.out.println("Third call count: " + products3.size());
	}

	public void enableQueryCache() {
		/*
		 * # Enable query cache spring.jpa.properties.hibernate.cache.use_query_cache=true
		 */

		/*
		 * public interface ProductRepository extends JpaRepository<Product, Long> {
		 * 
		 * @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
		 * 
		 * @Query("SELECT p FROM Product p WHERE p.price > :minPrice") List<Product>
		 * findExpensiveProducts(@Param("minPrice") BigDecimal minPrice); }
		 */
	}

}
