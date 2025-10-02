/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimService {

	private final ClaimRepository repo;

	private final PrepayPublisher publisher; // simple interface to "emit"

	private List<Claim> claims;

	public ClaimService(ClaimRepository repo, PrepayPublisher publisher) {
		this.repo = repo;
		this.publisher = publisher;
	}

	/**
	 * Ingest claim; idempotent by externalId.
	 */
	@Transactional
	public Claim ingestClaim(Claim incoming) {
		Optional<Claim> existing = repo.findByExternalId(incoming.externalId);
		if (existing.isPresent()) {
			return existing.get(); // idempotent: return stored
		}

		Claim saved = repo.save(incoming);

		// Detection logic: if professional → look for facility within 30 days future
		// If facility → look for professional within 30 days past
		if (ClaimType.PROFESSIONAL.toString().equalsIgnoreCase(saved.claimType.toString())) {
			// search for facility same patient in [claimDate, claimDate + 30]
			LocalDate from = saved.claimDate;
			LocalDate to = saved.claimDate.plusDays(30);
			List<Claim> matches = repo.findByPatientAndTypeBetween(saved.patientId, ClaimType.FACILITY.toString(), from,
					to);
			if (!matches.isEmpty()) {
				// found prepay candidate
				publisher.publishPrepayEvent(saved, matches);
			}
		}
		else if (ClaimType.FACILITY.toString().equalsIgnoreCase(saved.claimType.toString())) {
			// find prior professional claims [claimDate - 30, claimDate]
			LocalDate from = saved.claimDate.minusDays(30);
			LocalDate to = saved.claimDate;
			List<Claim> matches = repo.findByPatientAndTypeBetween(saved.patientId, ClaimType.PROFESSIONAL.toString(),
					from, to);
			if (!matches.isEmpty()) {
				publisher.publishPrepayEvent(saved, matches);
			}
		}

		return saved;
	}

	public ProcessResult processClaim(Claim claim) {
		return ProcessResult.builder().build();
	}

	public ProviderStats getProviderStatistics(String providerId) {
		return ProviderStats.builder().build();
	}

	// Using Stream API for claim processing
	public Map<ClaimStatus, List<Claim>> processClaims(List<Claim> claims) {
		return claims.stream()
			.filter(claim -> claim.getReviewDate() != null)
			.peek(this::applyClinicalRules)
			.collect(Collectors.groupingBy(Claim::getStatus));
	}

	// Parallel processing for performance
	public List<Claim> processClaimsParallel(List<Claim> claims) {
		return claims.parallelStream()
			.filter(claim -> claim.isPrePay() || claim.isOutpatient())
			.map(this::enrichClaimData)
			.collect(Collectors.toList());
	}

	private void applyClinicalRules(Claim claim) {
		// Clinical validation logic
		if (isCrossClaimEligible(claim)) {
			claim.setStatus(ClaimStatus.CCCR_ELIGIBLE);
		}
	}

	private Claim enrichClaimData(Claim claim) {
		// Data enrichment logic
		return claim;
	}

	private boolean isCrossClaimEligible(Claim claim) {
		// Business logic for CCCR eligibility
		return claim.getPhysicianSubmitTime().isBefore(claim.getHospitalSubmitTime());
	}

}
