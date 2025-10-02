/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author javau
 */
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

}
