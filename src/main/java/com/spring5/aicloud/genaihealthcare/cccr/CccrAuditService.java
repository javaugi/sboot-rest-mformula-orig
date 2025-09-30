/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

// Separate CccrAuditService listens for the event
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CccrAuditService {

    @Async // <-- Runs in a separate thread
    @EventListener
    public void handleClaimReviewedEvent(ClaimReviewedEvent event) {
        // auditRepository.logEvent(event.getClaim(), event.getResult(), "REVIEW_COMPLETE");
        // This can be slower without impacting the main workflow
    }
}
