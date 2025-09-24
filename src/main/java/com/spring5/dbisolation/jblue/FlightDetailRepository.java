/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightDetailRepository extends CosmosRepository<FlightDetail, String> {
    Optional<FlightDetail> findByFlightNumber(String flightNumber);
}
