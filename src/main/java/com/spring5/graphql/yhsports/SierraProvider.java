/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SierraProvider implements SportsDataProvider {

    @Override
    public List<SportsScore> getLiveScores(String league) {
        // Simulate a reliable legacy provider
        return List.of(
                new SportsScore("Lakers", 102, league),
                new SportsScore("Warriors", 98, league)
        );
    }
}
