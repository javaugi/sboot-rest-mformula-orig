package com.interview.hrank.veh.inventory;

import com.spring5.entity.Product;
import com.spring5.entity.RecalledProduct;
import com.spring5.secfilterconverter.ProductFilter;
import com.spring5.service.ProductService;
import com.spring5.service.RecalledProductService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Temporarily disabled for CICD")
public class NonRecalledProductServiceTests {

	@Autowired
	ProductService productService;

	@Autowired
	RecalledProductService recalledProductService;

	List<String> commonNames = Arrays.asList("product11", "product12");

	/**
	 * Helper method to create test products
	 */
	private Product createTestProduct(String productName, Double price, Integer quantity) {
		return Product.builder().name(productName).price(price).quantity(quantity).build();
	}

	/**
	 * Helper method to create test recalled products
	 */
	private RecalledProduct createTestRecalledProduct(String recalledProductName, Boolean expired) {
		return RecalledProduct.builder().name(recalledProductName).expired(expired).build();
	}

	@Test
	public void displayNonRecalledProducts() {
		createProducts();
		Set<String> names = createRecalledProducts();
		System.out.println("recalledProduct names: " + names);

		Collection<Product> allProducts = productService.getAllUnfilteredProduct();
		System.out.println("allProducts: " + allProducts);
		Assertions.assertNotNull(allProducts);

		Collection<RecalledProduct> recalledProducts = recalledProductService.getAllRecalledProducts();
		System.out.println("recalledProducts: " + recalledProducts);
		Assertions.assertNotNull(recalledProducts);

		List<Product> filteredProducts = allProducts.stream()
			.filter(product -> !names.contains(product.getName()))
			.collect(Collectors.toList());
		System.out.println("1 filtered: " + filteredProducts.size());
		Assertions.assertNotNull(filteredProducts);
		Assertions.assertTrue((filteredProducts.size() == (allProducts.size() - 2)),
				"1 filtered nonrecalledProducts size=" + filteredProducts.size());

		// test git push - another push
		ProductFilter filter = new ProductFilter(names);
		filteredProducts = filter.removeRecalledFrom(allProducts);
		System.out.println("2 filtered: " + filteredProducts.size());
		Assertions.assertTrue((filteredProducts.size() == (allProducts.size() - 2)),
				"2 filteredProducts size=" + filteredProducts.size());

		filteredProducts = new ArrayList(productService.getAllNonRecalledProduct());
		System.out.println("3 filtered: " + filteredProducts.size());
		Assertions.assertTrue((filteredProducts.size() == (allProducts.size() - 2)),
				"3 filteredProducts size=" + filteredProducts.size());
	}

	private void createProducts() {

		Product product;
		for (String name : commonNames) {
			product = createTestProduct(name, 1.3, 5);
			productService.save(product);
		}

		product = createTestProduct("product21", 1.3, 5);
		productService.save(product);
		product = createTestProduct("product22", 1.3, 5);
		productService.save(product);
		product = createTestProduct("product23", 1.3, 5);
		productService.save(product);
	}

	private Set<String> createRecalledProducts() {

		Set<String> names = new HashSet<>();
		RecalledProduct product;
		for (String name : commonNames) {
			product = createTestRecalledProduct(name, false);
			recalledProductService.save(product);
			names.add(name);
		}

		product = createTestRecalledProduct("recallproduct21", false);
		recalledProductService.save(product);
		names.add(product.getName());
		product = createTestRecalledProduct("recallproduct22", false);
		recalledProductService.save(product);
		names.add(product.getName());
		product = createTestRecalledProduct("recallproduct23", false);
		recalledProductService.save(product);
		names.add(product.getName());

		return names;
	}

}
