/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<Claim> ingest(@Valid @RequestBody Claim claim) {
        Claim saved = claimService.ingestClaim(claim);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/process")
    public ResponseEntity<ProcessResult> processClaim(@RequestBody Claim claim) {
        return ResponseEntity.ok(claimService.processClaim(claim));
    }

    @GetMapping("/stats/{providerId}")
    public ResponseEntity<ProviderStats> getProviderStats(@PathVariable String providerId) {
        return ResponseEntity.ok(claimService.getProviderStatistics(providerId));
    }
}
