/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.shoppingcart;

import com.spring5.entity.Product;
import java.util.HashMap;
import java.util.Map;

public class CoffeeMug extends Product {

    Integer size;
    String color;
    private Map<String, String[]> variants;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public Map<String, String[]> getVariants() {
        if (variants == null) {
            variants = new HashMap<>();
        }
        if (size != null) {
            variants.put("size", java.util.List.of(size.toString()).toArray(new String[1]));
        }
        if (color != null) {
            variants.put("color", java.util.List.of(color).toArray(new String[1]));
        }
        return variants;
    }

    @Override
    public void setVariants(Map<String, String[]> variants) {
        this.variants = variants;
    }
}
