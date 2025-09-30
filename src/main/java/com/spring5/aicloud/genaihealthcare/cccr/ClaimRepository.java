/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    public Optional<Claim> findByExternalId(String externalId);

    @Query(
            "select c from Claim c where c.patientId = :patientId and c.claimType = :claimType "
            + "and c.claimDate between :from and :to")
    public List<Claim> findByPatientAndTypeBetween(
            String patientId, String claimType, LocalDate from, LocalDate to);

    public List<Claim> findByEncounterId(String encounterId);
}
