/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Data // it includes @Getter @Setter @ToString @EqualsAndHashCode @RequiredArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
// 1. Using JPA with proper indexing
@Table(indexes = @Index(name = "idx_vin", columnList = "vin"))
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vin;
    private String make;
    private String model;
    private Long targetVersionId;

    @LastModifiedDate
    private LocalDateTime lastComminucation;
}
