/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

// 2. Using native query with query hints
@Repository
public interface AirlineRepository extends CosmosRepository<Airline, String> {

    Optional<Airline> findByAirlineCode(String airlineCode);

    List<Airline> findByAirlineCodeIn(Set<String> airlineCodes);
}
