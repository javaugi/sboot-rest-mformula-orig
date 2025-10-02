/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import com.spring5.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author javaugi
 */
@RestController
@RequiredArgsConstructor
public class WebMvcController {

	private final ProductService productService;

	private final ProductRepository productRepository;

	// to display all products at localhost:8080
	// to see database values at localhost:8080/h2-console
	@GetMapping
	public ResponseEntity<Collection<Product>> getAllProducts() {
		return ResponseEntity.ok(productService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		Optional<Product> productOptional = productRepository.findById(id);

		// *
		if (productOptional.isPresent()) {
			ResponseEntity.status(HttpStatusCode.valueOf(200)).body(productOptional.get());
			ResponseEntity.status(HttpStatus.OK).body(productOptional.get());
			new ResponseEntity<>(productOptional.get(), HttpStatus.OK);
			return ResponseEntity.ok(productOptional.get());
		}
		else {
			ResponseEntity.notFound().build();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the
																// product doesn't exist
		}
		// */
		// return ResponseEntity.ok(productOptional.orElse(null));

		// round(sqrt((power(max(lat_n) - min(lat_n)) + power(max(lat_n) - min(lat_n))),
		// 2), 4)
	}

	@GetMapping("/api/{id}")
	public ResponseEntity<Product> getProductByApiId(@PathVariable Long id) {
		return productRepository.findById(id).map(product -> {
			return ResponseEntity.ok(product);
		}).orElse(ResponseEntity.notFound().build());
		// return ResponseEntity.ok(productOptional.orElse(null));
	}

	@GetMapping("/api2/{id}")
	public ResponseEntity<Product> getProductByApi2Id(@PathVariable Long id) {
		return ResponseEntity.ok(productRepository.findById(id).orElse(null));
	}

	@GetMapping("/restProducts")
	public ResponseEntity<Collection<Product>> listProducts(HttpServletRequest request, ModelMap modelMap) {
		return ResponseEntity.ok(productRepository.findAll());
	}

	@GetMapping("/restProducts2")
	public ResponseEntity<Collection<Product>> listProducts2(org.springframework.http.RequestEntity<Product> request) {
		return ResponseEntity.ok(productRepository.findAll());
	}

	@PostMapping
	public ResponseEntity<Product> addProduct(org.springframework.http.RequestEntity<Product> request) {
		Product product = productRepository.save(request.getBody());

		ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(product);
		new ResponseEntity<>(product, HttpStatus.CREATED);
		ResponseEntity.status(HttpStatus.CREATED).body(product);
		// Return 201 + Location header
		ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(product);
		return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(product);
	}

	@PostMapping("/savenew")
	public ResponseEntity<Product> createNewProduct(@RequestBody Product product) {
		product = productRepository.save(product);
		ResponseEntity.status(HttpStatus.CREATED).body(product);
		return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(product);
	}

	@PutMapping
	public ResponseEntity<Product> updateProduct(org.springframework.http.RequestEntity<Product> request) {
		Product product = productRepository.save(request.getBody());
		ResponseEntity.status(HttpStatus.FOUND).body(product);
		return ResponseEntity.ok(product);
	}

	@RequestMapping(value = "/heavyresource/{id}", method = RequestMethod.PATCH,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> partialUpdateGeneric(@RequestBody Product productUpdates, @PathVariable("id") Long id) {
		Optional<Product> productOptional = productRepository.findById(id);

		// *
		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			BeanUtils.copyProperties(productUpdates, product, new String[] { "id" });
			product = productRepository.save(product);
			ResponseEntity.status(org.springframework.http.HttpStatusCode.valueOf(200)).body(product);
			return ResponseEntity.ok(product);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the
																// product doesn't exist
		}
		// */
		// return ResponseEntity.ok(productOptional.orElse(null));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> patchUpdate(@RequestBody Product productUpdates, @PathVariable("id") Long id) {
		Optional<Product> productOptional = productRepository.findById(id);

		// *
		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			BeanUtils.copyProperties(productUpdates, product, new String[] { "id" });
			product = productRepository.save(product);
			return ResponseEntity.ok(product);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the
																// product doesn't exist
		}
		// */
		// return ResponseEntity.ok(productOptional.orElse(null));
	}

	@DeleteMapping("/{id}") // Map DELETE requests to /products/{id}
	public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
		Optional<Product> productOptional = productRepository.findById(id);

		if (productOptional.isPresent()) {
			productRepository.deleteById(id); // Use deleteById for deleting by ID
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for
																// successful deletion
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the
																// product doesn't exist
		}
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteProduct(org.springframework.http.RequestEntity<Product> request) {
		Optional<Product> productOptional = Optional.empty();
		long id = 0;
		if (request.getBody() != null) {
			id = request.getBody().getId();
		}
		if (id > 0) {
			productOptional = productRepository.findById(id);
		}

		if (id > 0 && productOptional.isPresent()) {
			productRepository.deleteById(id); // Use deleteById for deleting by ID
			ResponseEntity.noContent().build();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for
																// successful deletion
		}
		else {
			ResponseEntity.notFound().build();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the
																// product doesn't exist
		}
	}

}

/*
 * /* Key Features
 * 
 * RESTful API with HATEOAS: Resources include links to related resources Follows REST
 * principles Self-descriptive messages
 * 
 * Web Interface with Spring MVC: Traditional server-side rendering Thymeleaf templates
 * for HTML generation Simple CRUD operations through web forms
 * 
 * Data Model: JPA entities with proper relationships Repository pattern for data access
 * 
 * Separation of Concerns: API endpoints separate from web interface Clear distinction
 * between data model and resource representation
 * 
 * This implementation provides a solid foundation that can be extended with additional
 * features like:
 * 
 * Authentication and authorization Validation Advanced search capabilities Pagination
 * Caching API documentation with Swagger
 */
