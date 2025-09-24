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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
@Transactional
@Service
@RequiredArgsConstructor    
public class ProductServiceImpl implements ProductService {
    private long nextId = 1;

    private final ProductRepository productRepository;
    //private final CacheManager cacheManager;
    
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
    
    @Cacheable(value = "products", key = "#page + '-' + #size")
    public Page<ProductDTO> getAllProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
                .map(this::convertToDTO);
    }
    
    @Cacheable(value = "product", key = "#id")
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @CacheEvict(value = {"products", "product"}, allEntries = true)
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
    
    
    private final Map<Long, Product> products = new HashMap<>();
    public Product saveProduct(Product product) {
        Product newProduct = new Product(nextId++, product.getName(), product.getPrice(), product.getQuantity(), product.getDescription(), product.isStatus());
        products.put(newProduct.getId(), newProduct);
        return newProduct;
    }
    
    public static Product createProduct(String name, BigDecimal price, int quantity, String description, boolean status) {
        return Product.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .description(description)
                .status(status)
                .build();
    }        
}
