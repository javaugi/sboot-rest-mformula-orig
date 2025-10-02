package com.interview.hrank.veh.inventory;

import com.spring5.entity.Product;
import com.spring5.entity.RecalledProduct;
import com.spring5.service.ProductService;
import com.spring5.service.RecalledProductService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Disabled("Temporarily disabled for CICD")
public class ProductServiceTests {

	@Autowired
	ProductService productService;

	@Autowired
	RecalledProductService recalledProductService;

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
	public void shouldSaveProduct() {
		Product product = createTestProduct("product1", 1.2, 2);

		Product savedProduct = productService.save(product);

		Product loadedProduct = productService.findById(savedProduct.getId()).orElse(null);

		Assertions.assertNotNull(loadedProduct);
		Assertions.assertNotNull(loadedProduct.getId());
	}

	@Test
	public void shouldUpdateProduct() {
		Product product = createTestProduct("product2", 1.3, 5);

		Product savedProduct = productService.save(product);

		Product loadedProduct = productService.findById(savedProduct.getId()).orElse(null);

		Assertions.assertNotNull(loadedProduct);

		loadedProduct.setName("NewProduct");

		productService.save(loadedProduct);

		Assertions.assertNotNull(productService.findById(loadedProduct.getId()).orElse(null));
	}

	// Write your tests below
	@Test
	public void shouldFindSaveProduct() {
		Product product = createTestProduct("product3_new", 1.3, 5);
		Product savedProduct = productService.save(product);

		Product loadedProduct = productService.findById(savedProduct.getId()).orElse(null);
		Assertions.assertNotNull(loadedProduct);

		List<Product> namedProducts = productService.findByName("product3_new");
		Assertions.assertNotNull(namedProducts);
	}

	@Test
	public void shouldFindPagedProducts() {
		Product product = createTestProduct("product3_paged", 1.3, 5);

		Product savedProduct = productService.save(product);
		Assertions.assertNotNull(savedProduct);

		Product loadedProduct = productService.findById(savedProduct.getId()).orElse(null);
		Assertions.assertNotNull(loadedProduct);

		List<Product> namedProducts = productService.findByName("product3_paged");
		Assertions.assertNotNull(namedProducts);
		Assertions.assertNotNull(namedProducts.size() == 1);
		Assertions.assertNotNull(namedProducts.get(0) instanceof Product);

		Page<Product> pagedProduct = productService.findAll(Pageable.ofSize(10));
		Assertions.assertNotNull(pagedProduct);
		Assertions.assertNotNull(pagedProduct.getTotalElements() == 1);
		Assertions.assertNotNull(pagedProduct.getContent().get(0) instanceof Product);

	}

	@Test
	public void shouldProductIdMatch() {
		Product product = createTestProduct("product3_idmatch", 1.3, 5);
		Product savedProduct = productService.save(product);
		Assertions.assertNotNull(savedProduct);
		System.out.println("saveProductId=" + savedProduct.getId());

	}

	@Test
	public void shouldUpdateTheProduct() {
		Product product = createTestProduct("productUpdate", 1.3, 5);

		Product savedProduct = productService.save(product);

		Product loadedProduct = productService.findById(savedProduct.getId()).orElse(null);
		Assertions.assertNotNull(loadedProduct);

		loadedProduct.setName("NewProduct");
		productService.save(loadedProduct);
		Assertions.assertNotNull(productService.findById(loadedProduct.getId()).orElse(null));

		loadedProduct.setName("NewProductName");
		productService.updateProduct(loadedProduct.getId(), loadedProduct);
		Assertions.assertNotNull(productService.findById(loadedProduct.getId()).orElse(null));
	}

	@Test
	public void displayProducts() {
		Product product = createTestProduct("productPaged", 1.3, 5);

		Product savedProduct = productService.save(product);

		Product loadedProduct = productService.findById(savedProduct.getId()).orElse(null);
		Assertions.assertNotNull(loadedProduct);

		PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("name"));
		Page<Product> pagedProduct = productService.findAll(pageRequest);
		Assertions.assertNotNull(pagedProduct);
		Assertions.assertNotNull(pagedProduct.getTotalElements() > 0);
		Assertions.assertNotNull(pagedProduct.getContent().get(0) instanceof Product);
	}

}
