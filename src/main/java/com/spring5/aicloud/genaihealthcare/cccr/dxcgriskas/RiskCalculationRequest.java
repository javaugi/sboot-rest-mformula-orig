/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class RiskCalculationRequest {

    @NotBlank
    private String memberId;

    @NotBlank
    private String modelType; // CMS-HCC, HHS-HCC, RX-HCC, MEDICARE, COMMERCIAL, ACA

    @NotEmpty
    private List<ClaimDiagnosis> diagnoses;

    private MemberDemographics demographics;
    private PharmacyClaims pharmacyData;

    @Data
    @Builder
    public static class ClaimDiagnosis {

        private String icd10Code;
        private LocalDate serviceDate;
        private String claimType; // IP, OP, PROF
        private String providerType;
    }

    @Data
    @Builder
    public static class MemberDemographics {

        private LocalDate dateOfBirth;
        private String gender;
        private String enrollmentType;
        private String geographicRegion;
        private String disabilityStatus;
    }
}
