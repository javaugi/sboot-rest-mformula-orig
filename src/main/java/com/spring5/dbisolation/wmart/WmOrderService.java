/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

// @Service
@RequiredArgsConstructor
public class WmOrderService {

	private final WmOrderRepository orderRepo;

	private final IdempotencyRepository idemRepo; // DB table with unique constraint on
													// key

	@Transactional
	public OrderDto createOrder(String key, CreateOrderRequest r) {
		Optional<Idempotency> existing = idemRepo.findByKey(key);
		if (existing.isPresent()) {
			// return stored response
			return existing.get().toDto();
		}
		Order order = new Order(r.getOrderId(), r.getStoreId(), r.getAmount(), OrderStatus.CREATED);
		orderRepo.save(order);

		Idempotency idem = new Idempotency(key, order.getId());
		idemRepo.save(idem);

		return OrderDto.from(order);
	}

}
