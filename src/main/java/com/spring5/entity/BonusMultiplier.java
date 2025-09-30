/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "bonus_multiplier")
public class BonusMultiplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Explicit and safe and recommended
    Long id;

    @Column(name = "score", unique = true, nullable = false)
    private String score;

    // The multiplier value for that score
    @Column(name = "multiplier", nullable = false)
    private double multiplier;

    private String comments;

    public BonusMultiplier(String score, double multiplier) {
        this.score = score;
        this.multiplier = multiplier;
    }

    public BonusMultiplier(Long id, String score, String comments) {
        this.id = id;
        this.score = score;
        this.comments = comments;
    }
}
