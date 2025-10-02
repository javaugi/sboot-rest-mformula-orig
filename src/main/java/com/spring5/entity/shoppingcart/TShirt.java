/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.shoppingcart;

import com.spring5.entity.Product;
import java.util.HashMap;
import java.util.Map;

public class TShirt extends Product {

	Integer size;

	String type;

	private Map<String, String[]> variants;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Map<String, String[]> getVariants() {
		if (variants == null) {
			variants = new HashMap<>();
		}
		if (size != null) {
			variants.put("size", java.util.List.of(size.toString()).toArray(new String[1]));
		}
		if (type != null) {
			variants.put("type", java.util.List.of(type).toArray(new String[1]));
		}
		return variants;
	}

	@Override
	public void setVariants(Map<String, String[]> variants) {
		this.variants = variants;
	}

}
