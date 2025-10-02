/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import com.spring5.entity.Inventory;
import com.spring5.repository.InventoryRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryReservationService {

	@Autowired
	private InventoryRepository inventoryRepository;

	private final Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

	private final Object lock = new Object();

	public boolean reserveItem(String itemId, int quantity) {
		synchronized (lock) {
			AtomicInteger current = inventory.computeIfAbsent(itemId, k -> new AtomicInteger(100));
			if (current.get() >= quantity) {
				current.addAndGet(-quantity);
				return true;
			}
			return false;
		}
	}

	@Transactional
	public boolean reserveItemWithDB(String itemId, int quantity) {
		Inventory item = inventoryRepository.findById(Long.valueOf(itemId)).orElseThrow();

		if (item.getAvailableQuantity() >= quantity) {
			item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
			inventoryRepository.save(item);
			return true;
		}
		return false;
	}

}
