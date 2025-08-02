/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
public interface VehicleConfigService {
    VehicleConfig getConfiguration(String brand, String model, String year);
    PriceCalculation calculatePrice(String configId, String zipCode);    
}
