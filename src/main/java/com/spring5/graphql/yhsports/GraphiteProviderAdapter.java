/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraphiteProviderAdapter implements SportsDataProvider {

    private final GraphiteApiClient graphiteApiClient;
    private final SierraProvider sierraProvider; // fallback target

    public GraphiteProviderAdapter(GraphiteApiClient graphiteApiClient, SierraProvider sierraProvider) {
        this.graphiteApiClient = graphiteApiClient;
        this.sierraProvider = sierraProvider;
    }

    /**
     * Circuit breaker named "graphite". If this method throws,
     * fallbackGetLiveScores will be invoked.
     *
     * @param league
     * @return List
     */
    @Override
    @CircuitBreaker(name = "graphite", fallbackMethod = "fallbackGetLiveScores")
    public List<SportsScore> getLiveScores(String league) {
        // call incompatible Graphite API then adapt to SportsScore
        List<GraphiteMatch> matches = graphiteApiClient.fetchMatches(league);

        return matches.stream()
                .map(m -> new SportsScore(mapTeamCodeToName(m.getTeamCode()), m.getPoints(), league))
                .collect(Collectors.toList());
    }

    /**
     * Fallback method called by Resilience4j when the circuit opens or an
     * exception is thrown. Signature: same params as original + last parameter
     * is Throwable
     */
    public List<SportsScore> fallbackGetLiveScores(String league, Throwable t) {
        // Log the failure, metrics, etc.
        System.err.println("Graphite provider failed for league=" + league + ". Reason: " + t.getMessage());
        // Fallback strategy: delegate to SierraProvider
        return sierraProvider.getLiveScores(league);
    }

    private String mapTeamCodeToName(String code) {
        return switch (code) {
            case "LA" ->
                "Lakers";
            case "GS" ->
                "Warriors";
            default ->
                code;
        };
    }
}
