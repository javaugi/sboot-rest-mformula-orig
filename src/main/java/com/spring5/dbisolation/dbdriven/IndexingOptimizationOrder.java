/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/*
// PostgreSQL query to analyze performance
EXPLAIN ANALYZE SELECT * FROM orders WHERE customer_id = 123 AND status = 'SHIPPED';

// Creating an index for better performance
CREATE INDEX idx_order_customer_status ON orders(customer_id, status);
 */
@Data
@Builder(toBuilder = true)
@Entity
@Table(
        name = "io_orders",
        indexes = {
            @Index(name = "idx_io_order_customer", columnList = "customer_id"),
            @Index(name = "idx_io_order_status", columnList = "status")
        })
// The Bottom Line: One and the Same in a Running System -  between indexes created directly on a
// database table and those defined in Hibernate entities
public class IndexingOptimizationOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private IndexingOptimizationCustomer customer;

    private String status;
    // other fields
    private LocalDateTime createdAt;

    // Using pagination in repository
    // @Query("SELECT o FROM IndexingOptimizationOrder o WHERE o.customer.id = :customerId ORDER BY
    // o.createdAt DESC")
    // Page<IndexingOptimizationOrder> findByCustomer(@Param("customerId") Long customerId, Pageable
    // pageable);
}
