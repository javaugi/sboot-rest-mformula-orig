/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import org.springframework.stereotype.Service;

@Service
public class MedicationService {
    
    public final MedicationRepository medicationRepository;
    
    public MedicationService(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }
    
    public Medication getMedication(long medicationId) {
        return medicationRepository.findById(medicationId).orElse(null);
    }
    
}
