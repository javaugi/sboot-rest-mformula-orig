/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql.yhsports;

@lombok.Data
@lombok.Builder
public class GraphiteMatch {

    private String teamCode;
    private int points;

    // ctor, getters
    public GraphiteMatch(String teamCode, int points) {
        this.teamCode = teamCode;
        this.points = points;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public int getPoints() {
        return points;
    }
}
