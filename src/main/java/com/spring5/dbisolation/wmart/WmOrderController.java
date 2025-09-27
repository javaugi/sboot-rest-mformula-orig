/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class WmOrderController {

    private final WmOrderService orderService;

    public WmOrderController(WmOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody CreateOrderRequest r) {
        OrderDto result = orderService.createOrder(idempotencyKey, r);
        return ResponseEntity.ok(result);
    }
}
