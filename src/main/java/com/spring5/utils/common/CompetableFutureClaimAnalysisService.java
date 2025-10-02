/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import com.spring5.aicloud.genaihealthcare.cccr.AnalysisResult;
import com.spring5.aicloud.genaihealthcare.cccr.Claim;
import com.spring5.aicloud.genaihealthcare.cccr.ClinicalData;
import com.spring5.aicloud.genaihealthcare.cccr.FinancialData;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class CompetableFutureClaimAnalysisService {

	/*
	 * When to Use CompletableFuture Instead While Kafka is superior for service
	 * orchestration, CompletableFuture still has its place:
	 * 
	 * // Use CompletableFuture for within-service parallel processing // when not dealing
	 * with Kafka streams
	 */
	public Mono<AnalysisResult> analyzeClaim(Claim claim) {
		return Mono.fromCallable(() -> {
			// CPU-intensive analysis that benefits from parallel processing
			CompletableFuture<ClinicalData> clinicalFuture = CompletableFuture
				.supplyAsync(() -> analyzeClinicalData(claim), ForkJoinPool.commonPool());

			CompletableFuture<FinancialData> financialFuture = CompletableFuture
				.supplyAsync(() -> analyzeFinancialData(claim), ForkJoinPool.commonPool());

			return CompletableFuture.allOf(clinicalFuture, financialFuture)
				.thenApply(voidResult -> new AnalysisResult(clinicalFuture.join(), financialFuture.join()))
				.get(); // Blocking call, but within dedicated thread pool
		}).subscribeOn(Schedulers.boundedElastic());
	}

	private ClinicalData analyzeClinicalData(Claim claim) {
		return ClinicalData.builder().claim(claim).build();
	}

	private FinancialData analyzeFinancialData(Claim claim) {
		return FinancialData.builder().claim(claim).build();
	}

}
