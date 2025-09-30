/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.hackerrank;

import com.spring5.service.PerformanceReviewService;
import com.spring5.validatorex.InvalidReviewScoreException;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
// @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
// @AutoConfigureMockMvc
public class PerformanceReviewServiceSpringBootTests {

    private PerformanceReviewService service = new PerformanceReviewService();

    @Test
    public void calculateBonus_Exceeds_ReturnsCorrectMultiplier() throws InvalidReviewScoreException {
        double result = service.calculateBonus("Exceeds");
        assertEquals(1.20, result);
    }

    // Test 2
    @Test
    public void calculateBonus_Meets_ReturnsCorrectMultiplier() throws InvalidReviewScoreException {
        double result = service.calculateBonus("Meets");
        assertEquals(1.00, result);
    }

    // Test 2
    @Test
    public void calculateBonus_Invalid_ReturnsCorrectMultiplier() {
        Exception exception
                = assertThrows(
                        InvalidReviewScoreException.class,
                        () -> {
                            service.calculateBonus("Invalid");
                            // assertEquals(1.00, result);
                        });

        // Assert on the exception's message
        assertEquals("Invalid performance score: Invalid", exception.getMessage());
    }
}
