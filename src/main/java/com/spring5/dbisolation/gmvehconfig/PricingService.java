/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    private final List<PricingRule> rules;

    public PricingService() {
        rules = new ArrayList<>();
    }

    public BigDecimal calculatePrice(VehicleConfig config) {
        BigDecimal price = config.getBasePrice();
        for (PricingRule rule : rules) {
            price = rule.apply(config, price);
        }
        return price;
    }
}
