/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

// SportsController.java
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sports")
public class SportsController {

    private final SportsDataProvider provider;

    public SportsController(SportsDataProvider provider) {
        this.provider = provider;
    }

    @GetMapping("/scores/{league}")
    public List<SportsScore> getScores(@PathVariable String league) {
        return provider.getLiveScores(league);
    }
}
