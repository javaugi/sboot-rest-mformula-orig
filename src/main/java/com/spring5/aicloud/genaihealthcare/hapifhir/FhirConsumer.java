/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FhirConsumer {

    private final PatientEventRepository repo;
    private final KafkaTemplate<String, String> kafka;
    private final FhirValidationService fhirValidationService;
    private final PseudonymService pseudonymService;

    private final FhirContext fhirContext = FhirContext.forR4();

    @KafkaListener(topics = "raw-fhir-events", groupId = "fhir-normalizer")
    public void onMessage(String fhirJson) {
        IParser parser = fhirContext.newJsonParser();
        IBaseResource res = parser.parseResource(fhirJson);
        String type = res.fhirType(); // .getResourceType().name();

        boolean isValid = fhirValidation(fhirJson);
        if (!isValid) {
            log.warn("fhirValidation failed");
        }

        PatientEvent e = new PatientEvent();
        if (res instanceof Claim) {
            Claim c = (Claim) res;
            // Example: map claim->patientId using insured party or patient reference
            e.patientId = extractPatientIdFromClaim(c);
            e.eventType = "CLAIM";
            e.payload = fhirJson;
            e.eventTime = Instant.now();
        } else if (res instanceof Encounter) {
            Encounter en = (Encounter) res;
            e.patientId = extractPatientIdFromEncounter(en);
            e.eventType = "ENCOUNTER";
            e.payload = fhirJson;
            e.eventTime = Instant.now();
        } else {
            e.patientId = "unknown";
            e.eventType = type;
            e.payload = fhirJson;
            e.eventTime = Instant.now();
        }
        // Persist normalized event for analytics
        repo.save(e);

        // Scoring microservice or scoring-results to dashboards, billing automation, clinician UI
        kafka.send("patient-events ", type, fhirJson);
    }

    private boolean fhirValidation(String fhirJson) {
        IParser parser = fhirContext.newJsonParser();
        IBaseResource res = parser.parseResource(fhirJson);

        try {
            // Minimal validation: resource type allowed?
            String resourceType = res.fhirType(); // .getResourceType().name();
            boolean isValid = fhirValidationService.validate();
        } catch (Exception ex) {

        }

        return true;
    }

    private String extractPatientIdFromClaim(Claim c) {
        if (c.hasPatient() && c.getPatient().hasReference()) {
            return pseudonymize(c.getPatient().getReference());
        }
        // fallback: insured party or provider; production: canonical normalization
        return "unknown";
    }

    private String extractPatientIdFromEncounter(Encounter en) {
        if (en.hasSubject() && en.getSubject().hasReference()) {
            return pseudonymize(en.getSubject().getReference());
        }
        return "unknown";
    }

    private String pseudonymize(String patientId) {
        String externalSafeId = pseudonymService.getPseudonym(patientId);
        return externalSafeId;
        // externalModelClient.sendData(externalSafeId, otherData);
    }
}
