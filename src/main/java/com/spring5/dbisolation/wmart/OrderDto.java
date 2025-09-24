/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    Long id;
    Long orderId;

    String storeId;
    BigDecimal amount;
    OrderStatus orderStatus;

    public OrderDto(String orderId,
        String storeId,
        String status) {

    }

    public static OrderDto from(Order order) {
        return OrderDto.builder().amount(order.amount).build();
    }
}
