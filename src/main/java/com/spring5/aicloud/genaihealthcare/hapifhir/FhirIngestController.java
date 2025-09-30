/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fhir")
@RequiredArgsConstructor
public class FhirIngestController {

    private final FhirContext fhirContext = FhirContext.forR4();
    private final KafkaTemplate<String, String> kafka;
    private final FhirValidationService fhirValidationService;

    @PostMapping(consumes = "application/fhir+json")
    public ResponseEntity<String> ingestFhir(@RequestBody String fhirJson) {
        // Basic parsing to ensure valid FHIR resource
        boolean isValid = fhirValidation(fhirJson);

        IParser parser = fhirContext.newJsonParser();
        IBaseResource res = parser.parseResource(fhirJson);

        // Minimal validation: resource type allowed?
        String resourceType = res.fhirType(); // .getResourceType().name();

        // Forward raw FHIR to Kafka (in prod, add schema, provenance, redaction)
        kafka.send("raw-fhir-events", resourceType, fhirJson);

        return ResponseEntity.accepted().body("accepted:" + resourceType);
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
}
