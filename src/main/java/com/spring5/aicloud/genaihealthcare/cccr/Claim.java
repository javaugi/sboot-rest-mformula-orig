/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

import com.spring5.entity.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "claims", indexes = { @Index(name = "idx_patient_date", columnList = "patientId, claimDate"),
		@Index(name = "idx_externalId", columnList = "externalId", unique = true) })
public class Claim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Patient patient;

	@Column(name = "patient_id", nullable = false)
	public String patientId;

	@Column(name = "external_id", nullable = false, unique = true)
	public String externalId; // external system claim id => supports idempotency

	@Column(name = "claim_date", nullable = false)
	public LocalDate claimDate;

	@Column(name = "encounter_id")
	public String encounterId; // optional: ties professional & facility claims

	public Instant receivedAt;

	private String diagnosisCode; // ICD-10 codes

	private String procedureCode;

	private String providerNpi;

	public ClaimType claimType;

	public Double billedAmount;

	public ClaimStatus status;

	public boolean prePay;

	public boolean outpatient;

	public ReviewType reviewType;

	public ProcessingStage processingStage;

	public LocalDateTime physicianSubmitTime;

	public LocalDateTime hospitalSubmitTime;

	public LocalDateTime serviceDate;

	public LocalDateTime SubmitTime;

	public LocalDate reviewDate;

}
