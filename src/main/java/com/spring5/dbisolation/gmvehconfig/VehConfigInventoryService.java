/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class VehConfigInventoryService {

    @Cacheable(value = "inventory", key = "#dealerId + '-' + #vehicleId")
    public InventoryItem getInventoryItem(String dealerId, String vehicleId) {
        // DB call
        return null;
    }

    @CacheEvict(value = "inventory", key = "#item.dealerId + '-' + #item.vehicleId")
    public void updateInventory(InventoryItem item) {
        // Update logic
    }
}
