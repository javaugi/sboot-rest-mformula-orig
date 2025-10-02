/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation;

import com.spring5.entity.Inventory;
import com.spring5.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Level8SerializationRead {

	private final InventoryRepository inventoryRepository;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void updateInventory(Long productId, int quantity) {
		// This transaction will be completely isolated from others
		Inventory inventory = inventoryRepository.findByProductId(productId);
		inventory.setQuantity(inventory.getQuantity() - quantity);
		inventoryRepository.save(inventory);
	}

}
