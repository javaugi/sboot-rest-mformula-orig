/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class EnrollmentService {

    private final LLMClient llm;
    private final ConsentService consentService;
    private final AuditService auditService;
    private final EnrollmentRepository repo;
    private final ResponsibleAIIndexService raiService;

    public EnrollmentService(LLMClient llm, ConsentService consentService, AuditService auditService,
        EnrollmentRepository repo, ResponsibleAIIndexService raiService) {
        this.llm = llm;
        this.consentService = consentService;
        this.auditService = auditService;
        this.repo = repo;
        this.raiService = raiService;
    }

    public EnrollmentResult enrollPatient(String patientId, String consentId, String email, boolean acceptMarketing, String freeText) {
        // 1. Consent enforcement
        boolean consentOk = consentService.hasGivenConsent(patientId, consentId, "loyalty_enrollment");
        if (!consentOk) {
            return EnrollmentResult.denied("Consent not present or insufficient");
        }

        // 2. Redact / strip PII before sending to LLM
        String safeFreeText = PIIUtils.redactPII(freeText);
        String prompt = buildPrompt(patientId, safeFreeText, acceptMarketing);

        // 3. LLM call
        Map<String, Object> meta = new HashMap<>();
        meta.put("temperature", 0.2);
        LLMResponse resp = llm.generate(prompt, meta);

        // 4. Audit
        auditService.recordPrompt("LoyaltyEnrollment", patientId, prompt, resp.text, resp.modelName, resp.provenance);

        // 5. Compute Responsible AI Index and persist
        Map<String, Double> derived = raiService.deriveScoresFromResponse(prompt, resp.text, resp.confidence, consentOk);
        String notes = "auto-derived scores";
        raiService.computeAndStore("LoyaltyEnrollment", resp.modelName, notes,
            derived.get("transparency"), derived.get("fairness"), derived.get("privacy"), derived.get("regulatory"));

        // 6. Interpret LLM response with a human-friendly explanation
        double confidence = resp.confidence;
        String explanation = buildExplanation(resp.text, confidence);

        // 7. Persist enrollment (hash email instead of storing raw)
        Enrollment e = new Enrollment();
        e.patientId = patientId;
        e.emailHash = PIIUtils.hashPII(email);
        e.marketingAccepted = acceptMarketing;
        repo.save(e);

        return EnrollmentResult.success(e.id.toString(), explanation, confidence);
    }

    private String buildPrompt(String patientId, String safeFreeText, boolean acceptMarketing) {
        String promptTemplate = "You are a healthcare-friendly assistant. Produce a short, respectful enrollment message to encourage loyalty program signup. "
            + "PatientId: %s\nPreferences: acceptsMarketing=%s\nFreeText: %s\n"
            + "Constraints: Do not ask for or reveal any PII, be concise (<=40 words), and include an opt-out line.";
        return String.format(promptTemplate, patientId, acceptMarketing, safeFreeText);
    }

    private String buildExplanation(String llmText, double confidence) {
        return String.format("LLM suggested text (confidence %.2f): %s", confidence, llmText);
    }

    // Result DTO internal
    public static class EnrollmentResult {

        public final boolean success;
        public final String idOrMessage;
        public final double confidence;
        public final String explanation;

        private EnrollmentResult(boolean success, String idOrMessage, double confidence, String explanation) {
            this.success = success;
            this.idOrMessage = idOrMessage;
            this.confidence = confidence;
            this.explanation = explanation;
        }

        public static EnrollmentResult success(String id, String explanation, double confidence) {
            return new EnrollmentResult(true, id, confidence, explanation);
        }

        public static EnrollmentResult denied(String message) {
            return new EnrollmentResult(false, message, 0.0, message);
        }
    }
}
