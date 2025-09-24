/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    Long id;

    Long orderId;

    String storeId;
    BigDecimal amount;
    OrderStatus orderStatus;
    String status;

    public Order(String storeId,
        BigDecimal amount,
        OrderStatus orderStatus) {

    }
    public Order(String orderId,
        String storeId,
        BigDecimal amount,
        OrderStatus orderStatus) {

    }

}
