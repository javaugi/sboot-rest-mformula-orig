/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spring5.graphql.yhsports;

import java.util.List;

public interface SportsDataProvider {

    List<SportsScore> getLiveScores(String league);
}
