/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

/**
 *
 * @author javau
 */
public class PatientDTOWithMask {
    private String name;

    @PHI
    private String ssn;

    @PHI(maskWith = "***EMAIL_MASKED***")
    private String email;

    @PHI
    private String phone;

    private String medicalNotes; // Not PHI

    // Constructors, getters, and setters
    public PatientDTOWithMask(String name, String ssn, String email, String phone, String medicalNotes) {
        this.name = name;
        this.ssn = ssn;
        this.email = email;
        this.phone = phone;
        this.medicalNotes = medicalNotes;
    }

}
