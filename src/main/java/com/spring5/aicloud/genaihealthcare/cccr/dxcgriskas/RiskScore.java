/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import com.spring5.entity.Patient;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "risk_scores")
@Data
@Builder(toBuilder = true)
public class RiskScore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Patient patient;

	private LocalDate calculationDate;

	private String modelType; // DxCG Medicare, Commercial, Rx

	private Double riskScore;

	private Double predictedCost;

	private String planType;

	private Integer year;

	@ElementCollection
	@CollectionTable(name = "risk_score_hierarchies")
	private Map<String, String> hierarchicalConditionCategories;

}
