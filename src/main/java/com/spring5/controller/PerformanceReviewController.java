/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.entity.BonusMultiplier;
import com.spring5.service.PerformanceReviewImprovedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class PerformanceReviewController {

    @Autowired
    private PerformanceReviewImprovedService performanceReviewService;

    // Constructor injection
    /*
    public PerformanceReviewController(PerformanceReviewImprovedService performanceReviewService) {
        this.performanceReviewService = performanceReviewService;
    }
    // */

    @GetMapping("/{id}")
    public BonusMultiplier getReview(@PathVariable Long id) {
        return performanceReviewService.getReviewById(id);
    }

    @PostMapping
    public BonusMultiplier createReview(@RequestBody BonusMultiplier review) {
        return performanceReviewService.createReview(review);
    }
}
