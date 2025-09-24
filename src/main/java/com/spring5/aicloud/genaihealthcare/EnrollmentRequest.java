/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    @NotBlank
    public String patientId;   // internal id (not PII)
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String consentId;   // reference to a consent record
    public boolean acceptMarketing;
    public String freeText;    // free text input from patient to be used for personalization
}
