/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.shoppingcart;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Map;

public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Explicit and safe and
														// recommended
	Long id;

	private Long productId;

	private String productName;

	private Map<String, String> variant;

	private int quantity;

	private double price;

	private double total;

	private String timestamp;

	public CartItem() {
	}

	public CartItem(Long id, Long productId, String productName, Map<String, String> variant, int quantity,
			double price, double total, String timestamp) {
		this.id = id;
		this.productId = productId;
		this.productName = productName;
		this.variant = variant;
		this.quantity = quantity;
		this.price = price;
		this.total = total;
		this.timestamp = timestamp;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Map<String, String> getVariant() {
		return variant;
	}

	public void setVariant(Map<String, String> variant) {
		this.variant = variant;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
