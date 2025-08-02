/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prescriptions/validate")
public class PrescriptionValidationController {

    private final ValidationService validationService;

    @Autowired
    public PrescriptionValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping
    public ResponseEntity<ValidationResult> validatePrescription(@RequestBody PrescriptionRequest request) {
        if (request.getOrderId() == null || request.getOrderId().isEmpty()) {
            // Basic validation for missing required fields
            ValidationResult errorResult = new ValidationResult();
            errorResult.setStatus(ValidationResult.Status.REJECTED);
            errorResult.setValidationMessages(List.of("Order ID is required."));
            return ResponseEntity.badRequest().body(errorResult);
        }

        ValidationResult result = validationService.validatePrescription(request);
        HttpStatus httpStatus = (result.getStatus() == ValidationResult.Status.REJECTED) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
        return new ResponseEntity<>(result, httpStatus);
    }
}