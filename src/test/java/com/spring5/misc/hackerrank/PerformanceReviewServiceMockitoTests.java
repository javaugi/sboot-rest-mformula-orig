/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.hackerrank;

import com.spring5.service.BonusConfig;
import com.spring5.service.PerformanceReviewImprovedService;
import com.spring5.validatorex.InvalidReviewScoreException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PerformanceReviewServiceMockitoTests {
    // Create a mock instance of BonusConfig

    @Mock
    private BonusConfig mockBonusConfig;

    // Inject the mock into the service instance
    @InjectMocks
    private PerformanceReviewImprovedService service;

    @Test
    void calculateBonus_Exceeds_CallsConfigAndReturnsValue() throws InvalidReviewScoreException {
        // 1. ARRANGE: Define the mock's behavior
        String testScore = "Exceeds";
        double expectedMultiplier = 1.20;
        when(mockBonusConfig.getMultiplierForScore(testScore)).thenReturn(expectedMultiplier);

        // 2. ACT: Call the method under test
        double result = service.calculateBonus(testScore);

        // 3. ASSERT: Verify the result and the interaction
        assertEquals(expectedMultiplier, result);
        // Verify that the service actually called the mock with the correct argument
        verify(mockBonusConfig).getMultiplierForScore(testScore);
    }

    @Test
    void calculateBonus_Meets_CallsConfigAndReturnsValue() throws InvalidReviewScoreException {
        String testScore = "Meets";
        double expectedMultiplier = 1.00;
        when(mockBonusConfig.getMultiplierForScore(testScore)).thenReturn(expectedMultiplier);

        double result = service.calculateBonus(testScore);

        assertEquals(expectedMultiplier, result);
        verify(mockBonusConfig).getMultiplierForScore(testScore);
    }

    @Test
    void calculateBonus_InvalidScore_ThrowsException() throws InvalidReviewScoreException {
        String invalidScore = "Invalid";
        // Make the mock throw the exception
        when(mockBonusConfig.getMultiplierForScore(invalidScore))
            .thenThrow(new InvalidReviewScoreException("Invalid score"));

        // Assert that the exception is thrown by the SERVICE, not just the mock
        assertThrows(InvalidReviewScoreException.class, () -> {
            service.calculateBonus(invalidScore);
        });
        verify(mockBonusConfig).getMultiplierForScore(invalidScore);
    }

    // Test for a new, dynamically added score
    @Test
    void calculateBonus_NewScore_ReturnsNewValue() throws InvalidReviewScoreException {
        String newScore = "Super Exceeds";
        double newMultiplier = 1.50;

        when(mockBonusConfig.getMultiplierForScore(newScore)).thenReturn(newMultiplier);

        double result = service.calculateBonus(newScore);

        assertEquals(newMultiplier, result);
        verify(mockBonusConfig).getMultiplierForScore(newScore);
    }
}
