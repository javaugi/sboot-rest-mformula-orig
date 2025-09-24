/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {

    List<LoyaltyProgram> findByPatientId(String patientId);

    @Query("SELECT l FROM LoyaltyProgram l WHERE l.privacyConsent = true")
    List<LoyaltyProgram> findWithPrivacyConsent();

    Long countByProgramName(String programName);
}
