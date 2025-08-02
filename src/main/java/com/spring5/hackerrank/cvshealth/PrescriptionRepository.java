/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.springframework.stereotype.Repository;

// Assumes PrescriptionOrder has an 'id' field annotated with @Id or equivalent
@Repository
public interface PrescriptionRepository extends CosmosRepository<PrescriptionOrder, String> {
    // Custom query methods if needed, e.g.,
    // List<PrescriptionOrder> findByPatientId(String patientId);
}
