/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.entity.Product;
import com.spring5.entity.shoppingcart.CartItem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

	private final List<CartItem> cart = new CopyOnWriteArrayList<>();

	@Autowired
	private ProductService productService;

	public List<CartItem> getCart() {
		return new ArrayList<>(cart);
	}

	public CartItem addToCart(Long productId, java.util.Map<String, String> variant, int quantity) {
		Optional<Product> opt = productService.getProductById(productId);
		if (!opt.isPresent()) {
			throw new IllegalArgumentException("Product not found: " + productId);
		}

		Product product = opt.get();
		// Calculate price (in real app, you might have different pricing based on
		// variants)
		double price = product.getBasePrice();

		CartItem cartItem = new CartItem();
		cartItem.setId(System.currentTimeMillis());
		cartItem.setProductId(productId);
		cartItem.setProductName(product.getName());
		cartItem.setVariant(variant);
		cartItem.setQuantity(quantity);
		cartItem.setPrice(price);
		cartItem.setTotal(price * quantity);
		cartItem.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

		cart.add(cartItem);
		return cartItem;
	}

	public Optional<CartItem> updateQuantity(String itemId, int quantity) {
		if (quantity <= 0) {
			removeFromCart(itemId);
			return Optional.empty();
		}

		for (CartItem item : cart) {
			if (item.getId().equals(itemId)) {
				item.setQuantity(quantity);
				item.setTotal(item.getPrice() * quantity);
				return Optional.of(item);
			}
		}

		return Optional.empty();
	}

	public boolean removeFromCart(String itemId) {
		return cart.removeIf(item -> item.getId().equals(itemId));
	}

	public void clearCart() {
		cart.clear();
	}

	public int getCartCount() {
		return cart.size();
	}

	public double calculateSubtotal() {
		return cart.stream().mapToDouble(CartItem::getTotal).sum();
	}

}
