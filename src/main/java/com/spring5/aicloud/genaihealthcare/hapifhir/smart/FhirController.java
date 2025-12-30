/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.HashMap;
import java.util.Map;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fhir")
public class FhirController {

    @Autowired
    private FhirClientService fhirClientService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/patient")
    public ResponseEntity<String> getPatientData(
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authentication) {

        // Get access token from OAuth2 client
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        if (client == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = client.getAccessToken().getTokenValue();

        try {
            // Create FHIR client with access token
            IGenericClient fhirClient = fhirClientService.createFhirClient(accessToken);

            // Get patient ID from launch context (stored in OAuth2User attributes)
            String patientId = principal.getAttribute("patient");

            if (patientId == null) {
                return ResponseEntity.badRequest().body("No patient context available");
            }

            // Fetch patient data
            Patient patient = fhirClient.read()
                    .resource(Patient.class)
                    .withId(patientId)
                    .execute();

            // Fetch observations for the patient
            Bundle observations = fhirClient.search()
                    .forResource(Observation.class)
                    .where(Observation.PATIENT.hasId(patientId))
                    .returnBundle(Bundle.class)
                    .execute();

            // Process and return data
            Map<String, Object> response = new HashMap<>();
            response.put("patient", patient);
            response.put("observations", observations.getEntry());

            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching FHIR data: " + e.getMessage());
        }
    }

    @GetMapping("/metadata")
    public ResponseEntity<String> getCapabilityStatement(
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authentication) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();
        IGenericClient fhirClient = fhirClientService.createFhirClient(accessToken);

        // Get FHIR server metadata/CapabilityStatement        
        //Metadata metadata = fhirClient.capabilities().ofType(Metadata.class).execute();

        CapabilityStatement metadata = fhirClient.capabilities()
                .ofType(CapabilityStatement.class) // Use the concrete class for R4
                .execute();

        return ResponseEntity.ok(metadata.toString());
    }
}
