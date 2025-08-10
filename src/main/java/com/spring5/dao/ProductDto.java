/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dao;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductDto {
    private long id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String description;
    private boolean status;
}
