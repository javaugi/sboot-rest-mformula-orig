/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import com.spring5.audit.AuditOrder;
import com.spring5.audit.AuditOrderRepository;
import com.spring5.audit.OrderRequest;
import com.spring5.kafkamicroservice.PaymentRepository;
import com.spring5.validatorex.InsufficientStockException;
import com.spring5.validatorex.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/*
Transaction Management Questions
6. Explain Spring transaction management and isolation levels
Detailed Description:
Spring provides declarative and programmatic transaction management. Key isolation levels:
    READ_UNCOMMITTED
    READ_COMMITTED (default for most databases)
    REPEATABLE_READ
    SERIALIZABLE
 */
@Service
public class TransManIsolationLevelOrderService {

    @Autowired
    private AuditOrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRED,
            rollbackFor = {PaymentException.class, InsufficientStockException.class})
    public AuditOrder processOrder(OrderRequest request) {
        AuditOrder order = createOrder(request);
        processPayment(order);
        updateInventory(order);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPayment(AuditOrder order) {
        // payment processing logic
    }

    private AuditOrder createOrder(OrderRequest request) {
        return AuditOrder.builder().build();
    }

    private void updateInventory(AuditOrder order) {
    }
}
