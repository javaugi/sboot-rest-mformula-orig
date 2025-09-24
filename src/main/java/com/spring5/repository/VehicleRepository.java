/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author javau
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
