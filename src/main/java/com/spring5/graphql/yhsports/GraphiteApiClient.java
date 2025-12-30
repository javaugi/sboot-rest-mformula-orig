/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@lombok.RequiredArgsConstructor
public class GraphiteApiClient {

    private final SportsScoreRepository scoreRepo;

    // In real code you'd use WebClient/RestTemplate + DTOs + error handling
    public List<GraphiteMatch> fetchMatches(String sport) {
        // Simulate a remote call which *may* fail
        // In tests, throw new RuntimeException("Graphite down");
        List<GraphiteMatch> returnValue = scoreRepo.findAllByLeague(sport)
                .stream()
                .map(score -> GraphiteMatch.builder().teamCode(score.getTeam()).points(score.getScore()).build())
                .collect(Collectors.toList());

        return returnValue;
    }
}
