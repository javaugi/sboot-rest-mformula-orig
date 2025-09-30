/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.hackerrank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring5.controller.PerformanceReviewController;
import com.spring5.entity.BonusMultiplier;
import com.spring5.service.PerformanceReviewImprovedService;
import org.junit.jupiter.api.Disabled;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 1. This annotation slices the application context to only load beans relevant to the web layer
// (Controllers, WebMvcConfigurer, etc.)
// It auto-configures MockMvc which is the main entry point for server-side Spring MVC tests.
@WebMvcTest(PerformanceReviewController.class) // Focuses only on this Controller
@Disabled("MockitoBean not injected")
public class PerformanceReviewControllerWebMvcITests {

    // 2. Main utility to mock and perform HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // 3. This is the KEY. @MockBean adds a mock of this service to the Spring application context.
    // The controller will inject this mock instead of the real implementation.
    @MockitoBean
    private PerformanceReviewImprovedService performanceReviewService;

    // 4. Useful for converting objects to/from JSON
    @Autowired
    private ObjectMapper objectMapper;

    // @Test
    public void getReview_ShouldReturnReview() throws Exception {
        // Given
        Long reviewId = 1L;
        BonusMultiplier mockReview = new BonusMultiplier(reviewId, "Meets", "Solid performance");

        // Mock the service's behavior
        given(performanceReviewService.getReviewById(reviewId)).willReturn(mockReview);

        // When & Then
        mockMvc
                .perform(
                        get("/api/reviews/" + reviewId) // Perform GET request
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.id").value(reviewId)) // Validate JSON response body
                .andExpect(jsonPath("$.score").value("Meets"))
                .andExpect(jsonPath("$.comments").value("Solid performance"));
    }

    // @Test
    public void createReview_ShouldReturnCreatedReview() throws Exception {
        // Given
        BonusMultiplier reviewToCreate = new BonusMultiplier(null, "Exceeds", "Exceeded expectations");
        BonusMultiplier savedReview = new BonusMultiplier(1L, "Exceeds", "Exceeded expectations");

        // Mock the service's behavior
        given(performanceReviewService.createReview(any(BonusMultiplier.class)))
                .willReturn(savedReview);

        // When & Then
        mockMvc
                .perform(
                        post("/api/reviews") // Perform POST request
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                reviewToCreate))) // Convert object to JSON string
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L)) // Expect the mock saved object with an ID
                .andExpect(jsonPath("$.score").value("Exceeds"))
                .andExpect(jsonPath("$.comments").value("Exceeded expectations"));
    }
}
