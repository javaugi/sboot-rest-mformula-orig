/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import com.spring5.entity.shoppingcart.CartItem;
import com.spring5.entity.shoppingcart.CustomerInfo;
import com.spring5.entity.shoppingcart.PaymentInfo;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private List<CartItem> items;
    private CustomerInfo customerInfo;
    private PaymentInfo paymentInfo;
    private String subtotal;
    private String tax;
    private String shipping;
    private String total;
    private String orderDate;

    public Order(String storeId, BigDecimal amount, OrderStatus orderStatus) {
    }

    public Order(String orderId, String storeId, BigDecimal amount, OrderStatus orderStatus) {
    }
}
