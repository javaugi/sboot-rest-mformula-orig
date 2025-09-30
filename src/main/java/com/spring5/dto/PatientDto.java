/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dto;

import com.spring5.aicloud.genaihealthcare.cccr.Claim;
import com.spring5.entity.Appointment;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {

    Long id;
    Long version;
    private String memberId;
    private String name;
    private String firstName;
    private String lastName;
    private String ssn;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String userEmail;
    private String phoneNumber;
    private String planType;
    private List<Appointment> appointments;
    private Claim claim;
    private List<Claim> claims;
}
