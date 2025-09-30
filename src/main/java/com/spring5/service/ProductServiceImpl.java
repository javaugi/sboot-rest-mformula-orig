/*
 * Copyright (C) 2019 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.spring5.service;

import com.spring5.dto.ProductDTO;
import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
@Transactional
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private long nextId = 1;
    private final Map<Long, Product> products = new HashMap<>();

    private final ProductRepository productRepository;

    // private final CacheManager cacheManager;
    public void init() {
        initializeProducts();
    }

    private void initializeProducts() {
        // Coffee Mugs
        Map<String, String[]> mugVariants = new HashMap<>();
        mugVariants.put("sizes", new String[]{"8oz", "12oz", "16oz"});
        mugVariants.put("colors", new String[]{"Red", "Green", "White", "Black"});

        Random rand = new Random();
        Product coffeeMugs
                = new Product(
                        rand.nextLong(100000),
                        "Coffee Mugs",
                        "mugs",
                        "High-quality ceramic coffee mugs",
                        12.99,
                        mugVariants);

        // T-Shirts
        Map<String, String[]> shirtVariants = new HashMap<>();
        shirtVariants.put("types", new String[]{"Men", "Women", "Boys", "Girls"});
        shirtVariants.put("sizes", new String[]{"S", "M", "L"});
        shirtVariants.put("colors", new String[]{"Pink", "Orange", "Blue"});

        Product tShirts
                = new Product(
                        rand.nextLong(100000),
                        "T-Shirts",
                        "clothing",
                        "Comfortable cotton t-shirts",
                        19.99,
                        shirtVariants);

        // Soccer Balls
        Map<String, String[]> ballVariants = new HashMap<>();
        ballVariants.put("colors", new String[]{"Green", "Orange", "Purple"});

        Product soccerBalls
                = new Product(
                        rand.nextLong(100000),
                        "Soccer Balls",
                        "sports",
                        "Professional quality soccer balls",
                        24.99,
                        ballVariants);

        products.put(coffeeMugs.getId(), coffeeMugs);
        products.put(tShirts.getId(), tShirts);
        products.put(soccerBalls.getId(), soccerBalls);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        return product;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#page + '-' + #size")
    public Page<ProductDTO> getAllProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size)).map(this::convertToDTO);
    }

    @Cacheable(value = "product", key = "#id")
    public Optional<ProductDTO> getProductDtoById(Long id) {
        return productRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public boolean productExists(Long id) {
        return products.containsKey(id) || productRepository.findById(id).isPresent();
    }

    @CacheEvict(
            value = {"products", "product"},
            allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        product = productRepository.save(product);
        return convertToDTO(product);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    // private final Map<Long, Product> products = new HashMap<>();
    public Product saveProduct(Product product) {
        Product newProduct
                = new Product(
                        nextId++,
                        product.getName(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getDescription(),
                        product.isStatus());
        products.put(newProduct.getId(), newProduct);
        return newProduct;
    }

    public static Product createProduct(
            String name, BigDecimal price, int quantity, String description, boolean status) {
        return Product.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .description(description)
                .status(status)
                .build();
    }
}
