/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.validatorex.InvalidReviewScoreException;

public interface BonusConfig {

	double getMultiplierForScore(String score) throws InvalidReviewScoreException;

}
