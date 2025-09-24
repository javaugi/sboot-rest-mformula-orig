/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.entity.BonusMultiplier;
import com.spring5.repository.BonusRepository;
import com.spring5.validatorex.InvalidReviewScoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PerformanceReviewImprovedService {
    private final BonusConfig bonusConfig;
    private final BonusRepository bonusRepo;

    public double calculateBonus(String score) throws InvalidReviewScoreException {
        // The service now delegates the logic to the config source.
        // This is the Single Responsibility Principle: the service orchestrates, the config provides the values.
        return bonusConfig.getMultiplierForScore(score);
    }

    public BonusMultiplier getReviewById(Long id) {
        return bonusRepo.findById(id).orElse(new BonusMultiplier());
    }

    public BonusMultiplier createReview(BonusMultiplier review) {
        return bonusRepo.save(review);
    }
}
