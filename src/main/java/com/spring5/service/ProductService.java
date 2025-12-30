/*
 * Copyright (C) 2019 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.spring5.service;

import com.spring5.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author: $LastChangedBy
 */
public interface ProductService {

	Product save(Product product);

	List<Product> saveAll(List<Product> products);

	List<Product> findAll();

	List<Product> getAllUnfilteredProduct();

	List<Product> findByName(String name);

	Optional<Product> getProductById(Long productId);

	boolean productExists(Long id);

	Optional<Product> findById(Long id);

	public Page<Product> findAll(Pageable pageable);

	public List<Product> getAllNonRecalledProduct();

	public Product updateProduct(Long id, Product update);

    public void delete(Product product);
    public void deleteById(Long id);
}
