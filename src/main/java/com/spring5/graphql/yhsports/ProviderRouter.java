/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

// ProviderRouter.java (runtime switch without replacing bean)
import org.springframework.stereotype.Component;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Component
public class ProviderRouter implements SportsDataProvider {

    private final SierraProvider sierra;
    private final GraphiteProviderAdapter graphite;
    private volatile String providerFlag; // update from config/feature flag service

    public ProviderRouter(SierraProvider sierra, GraphiteProviderAdapter graphite,
            @Value("${sports.provider:SIERRA}") String providerFlag) {
        this.sierra = sierra;
        this.graphite = graphite;
        this.providerFlag = providerFlag;
    }

    // method to update providerFlag at runtime (e.g., from actuator endpoint or feature flag webhook)
    public void setProviderFlag(String providerFlag) {
        this.providerFlag = providerFlag;
    }

    @Override
    public List<SportsScore> getLiveScores(String league) {
        if ("GRAPHITE".equalsIgnoreCase(providerFlag)) {
            return graphite.getLiveScores(league);
        }
        return sierra.getLiveScores(league);
    }
}
