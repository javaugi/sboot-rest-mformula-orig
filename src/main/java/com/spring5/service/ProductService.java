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

/**
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public interface ProductService {

    Product save(Product product);

    List<Product> saveAll(List<Product> products);

    List<Product> findAll();

    List<Product> findByName(String name);

    Optional<Product> getProductById(Long productId);

    boolean productExists(Long id);
}
