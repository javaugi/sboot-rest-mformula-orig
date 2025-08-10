/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Cacheable
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ Explicit and safe and recommended
    Long id;
    /*
    What @GeneratedValue(strategy = GenerationType.IDENTITY) Does
        Purpose: Tells JPA to use the database’s native auto-increment (e.g., MySQL AUTO_INCREMENT, PostgreSQL SERIAL).
        Behavior:
            The database generates the ID value.
            JPA retrieves the ID after insertion (requires a separate SELECT in some databases).

    Best practice: Always specify strategy for: (1) Clarity. (2) Database portability.

    IDENTITY AND oTHER Alternatives to IDENTITY
        Strategy                    Database Support        Use Case
        GenerationType.IDENTITY     h2/mYsql/PostgreSQL
        GenerationType.SEQUENCE     Oracle,                 PostgreSQL	High-performance bulk inserts.
        GenerationType.TABLE        All databases           Legacy systems (slow, not recommended).
        GenerationType.AUTO         All databases           Lets JPA pick (least predictable).
     */

    private String accountType;    
    private BigDecimal balance;
}
