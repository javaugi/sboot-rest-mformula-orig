/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Idempotency {

	Long id;

	String key;

	Long orderId;

	Order order;

	public Idempotency(String key, Long orderid) {
	}

	public OrderDto toDto() {
		return OrderDto.builder().amount(order.amount).build();
	}

}
