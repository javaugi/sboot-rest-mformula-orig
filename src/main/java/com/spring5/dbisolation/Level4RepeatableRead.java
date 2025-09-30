/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation;

import com.spring5.audit.AuditOrder;
import com.spring5.audit.AuditOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Level4RepeatableRead {

    private final AuditOrderRepository orderRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void processOrder(Long orderId) {
        // First read
        AuditOrder order = orderRepository.findById(orderId).orElseThrow();

        // Some business logic...
        // Second read - guaranteed to see the same data as first read
        AuditOrder sameOrder = orderRepository.findById(orderId).orElseThrow();

        // But new rows may have been inserted by other transactions (phantom reads)
    }
}
