/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.mapper;

import com.spring5.dto.ProductDTO;
import com.spring5.entity.Product;

/**
 * @author javau
 */
public class ProductMapperMain {

    Product product = new Product();
    ProductDTO prodDto = ProductMapper.INSTANCE.toDto(product);
    Product prod = ProductMapper.INSTANCE.toEntity(prodDto);
}
