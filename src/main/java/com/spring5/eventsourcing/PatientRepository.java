/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.util.UUID;

/**
 * @author javau
 */
public interface PatientRepository {

    PatientRecord getPatientRecord(UUID patientId);

    void savePatientRecord(PatientRecord patientRecord);
}
