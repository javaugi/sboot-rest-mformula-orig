/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import com.spring5.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author javaugi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/restproduct")
public class ProductRestController {
    
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    // to display all products at localhost:8080
    // to see database values at localhost:8080/h2-console    
    @GetMapping
    public ResponseEntity<Collection<Product>> getAllProducts(HttpServletRequest request) {
        List<Product> products = productService.findAll();
        String name = request.getParameter("name");
        if (name == null || name.isEmpty()) {
            return ResponseEntity.ok(products);
        }
        
        products = products.stream().filter(p -> name.contains(p.getName()))
                .collect(Collectors.toList());
                        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        //*
        if (productOptional.isPresent()) {
            return ResponseEntity.ok(productOptional.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
        // */
        //return ResponseEntity.ok(productOptional.orElse(null));
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
        return ResponseEntity.ok(product);
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(org.springframework.http.RequestEntity<Product> request) {
        Product product = productRepository.save(request.getBody());
        return ResponseEntity.ok(product);
    } 
    
    
    @RequestMapping(value = "/heavyresource/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> partialUpdateGeneric(@RequestBody Product productUpdates, @PathVariable("id") Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        //*
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            BeanUtils.copyProperties(productUpdates, product, new String[]{"id"});
            product = productRepository.save(product);
            return ResponseEntity.ok(product);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
        // */
        //return ResponseEntity.ok(productOptional.orElse(null));
    }    
    
    @DeleteMapping("/{id}") // Map DELETE requests to /products/{id}
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            productRepository.deleteById(id); // Use deleteById for deleting by ID
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
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
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } else {
            ResponseEntity.notFound().build();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
    }
}

/*
In REST, the PATCH HTTP method is used for partial updates of a resource, while the PUT method is used for complete replacements of a 
    resource. PUT replaces the entire resource with the provided data, while PATCH only updates the specific fields specified in the request body. 
Elaboration:
PUT:    
        Replaces the entire resource with a new version.
        Expects the entire resource data to be provided in the request body.
        Idempotent: Multiple identical PUT requests have the same effect as a single one. 
PATCH:
        Applies partial updates to a resource.
        Only the fields to be modified are included in the request body. 
        Not necessarily idempotent: Repeated PATCH requests can lead to different results depending on the order of operations. 
Example:
    If you want to update the email address of a user, you would use a PATCH request, sending only the new email address. A PUT request 
        would require sending the entire user profile, including the unchanged fields, according to GeeksForGeeks. 
    In essence, use PUT when you need to completely replace a resource, and PATCH when you only need to modify specific parts of it. 
*/