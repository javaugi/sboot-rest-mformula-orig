/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReactiveClaimValidationService {

	private final ReactiveKafkaProducer kafkaProducer;

	private final ReactiveClaimEventRepository claimRepository;

	public ReactiveClaimValidationService(ReactiveKafkaProducer kafkaProducer,
			ReactiveClaimEventRepository claimRepository) {
		this.kafkaProducer = kafkaProducer;
		this.claimRepository = claimRepository;
	}

	@KafkaListener(topics = "${app.topics.claim-submitted}")
	public void processClaimSubmitted(ReactiveClaimEvent event) {
		log.info("Processing submitted claim: {}", event.getId());

		validateClaim(event).flatMap(isValid -> {
			if (isValid) {
				ReactiveClaimEvent validatedEvent = new ReactiveClaimEvent();
				BeanUtils.copyProperties(event, validatedEvent);
				validatedEvent.setStatus(ReactiveClaimEvent.ClaimStatus.VALIDATED);
				validatedEvent.setFailureReason(null);

				return kafkaProducer.sendClaimValidated(validatedEvent);
			}
			else {
				return kafkaProducer.sendToDeadLetterTopic(event.getId(), "Validation failed");
			}
		}).onErrorResume(error -> {
			log.error("Error processing claim {}: {}", event.getId(), error.getMessage());
			return kafkaProducer.sendToDeadLetterTopic(event.getId(), "Processing error: " + error.getMessage());
		}).subscribe();
	}

	private Mono<Boolean> validateClaim(ReactiveClaimEvent event) {
		return Mono.fromCallable(() -> claimRepository.existsById(event.getId())).flatMap(optionalClaim -> {
			if (optionalClaim) {
				// Claim already exists
				return Mono.just(false);
			}
			else {
				// Claim doesn't exist, validate clinical data
				return validateClinicalData(event);
			}
		});
	}

	private Mono<Boolean> validateClaimOpt2(ReactiveClaimEvent event) {
		return Mono.just(claimRepository.existsById(event.getId()))
			.flatMap(optionalClaim -> {
				if (optionalClaim) {
					return Mono.just(false);
				}																	
				return validateClinicalData(event); 
	});
	}

	private Mono<Boolean> validateClaimOpt3(ReactiveClaimEvent event) {
		return Mono.justOrEmpty(claimRepository.existsById(event.getId()))
			.hasElement() // Convert to Mono<Boolean> indicating presence
			.flatMap(exists -> {
				if (exists) {
					return Mono.just(false);
				}
				else {
					return validateClinicalData(event);
				}
			});
	}

	private Mono<Boolean> validateClinicalData(ReactiveClaimEvent event) {
		// Complex validation logic using reactive database calls
		return Mono.just(true); // Simplified
	}

}
