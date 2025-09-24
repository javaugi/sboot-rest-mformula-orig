/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReactiveClaimEvent {

    String id;
    String patientId;
    String providerId;
    ClaimType claimType;
    BigDecimal amount;
    LocalDateTime serviceDate;
    ClaimStatus status;
    String failureReason;


    public enum ClaimType {
        PHYSICIAN, HOSPITAL, OUTPATIENT
    }

    public enum ClaimStatus {
        SUBMITTED, VALIDATED, APPROVED, REJECTED, PROCESSED
    }
}
