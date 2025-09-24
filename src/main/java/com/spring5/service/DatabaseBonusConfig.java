/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.repository.BonusRepository;
import com.spring5.validatorex.InvalidReviewScoreException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseBonusConfig implements BonusConfig {

    private final BonusRepository bonusRepo; // Assume a JPA Repository

    public DatabaseBonusConfig(BonusRepository bonusRepo) {
        this.bonusRepo = bonusRepo;
    }

    @Override
    public double getMultiplierForScore(String score) throws InvalidReviewScoreException {
        // Fetch the value from the database. If not found, throw exception.
        return bonusRepo.findByScore(score)
            //.map(BonusMultiplier::getMultiplier)
            .orElseThrow(() -> new InvalidReviewScoreException("Invalid performance score: " + score)).getMultiplier();
    }
}
