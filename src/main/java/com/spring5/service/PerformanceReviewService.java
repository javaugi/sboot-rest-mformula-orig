/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.validatorex.InvalidReviewScoreException;
import org.springframework.stereotype.Service;

@Service
public class PerformanceReviewService {
    public double calculateBonus(String score) throws InvalidReviewScoreException {
        switch (score) {
            case "Super Exceeds" -> {
                return 1.50;
            }
            case "Exceeds" -> {
                return 1.20;
            }
            case "Meets" -> {
                return 1.00;
            }
            case "Needs Improvement" -> {
                return 0.50;
            }
            default ->
                throw new InvalidReviewScoreException("Invalid performance score: " + score);

        }

    }
}
