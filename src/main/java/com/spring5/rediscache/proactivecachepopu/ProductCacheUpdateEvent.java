/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import org.springframework.context.ApplicationEvent;

public class ProductCacheUpdateEvent extends ApplicationEvent {

	private final Long productId;

	public ProductCacheUpdateEvent(Object source, Long productId) {
		super(source);
		this.productId = productId;
	}

	public Long getProductId() {
		return productId;
	}

}
