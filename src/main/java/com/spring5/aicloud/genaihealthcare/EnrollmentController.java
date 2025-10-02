/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import com.spring5.aicloud.genaihealthcare.EnrollmentService.EnrollmentResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aiapi/enrollments")
public class EnrollmentController {

	private final EnrollmentService service;

	public EnrollmentController(EnrollmentService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest req) {
		EnrollmentResult r = service.enrollPatient(req.patientId, req.consentId, req.email, req.acceptMarketing,
				req.freeText);
		EnrollmentResponse resp = new EnrollmentResponse();
		resp.enrollmentId = r.success ? r.idOrMessage : null;
		resp.success = r.success;
		resp.message = r.success ? "Enrolled" : r.idOrMessage;
		resp.confidenceScore = r.confidence;
		resp.explanation = r.explanation;
		return ResponseEntity.ok(resp);
	}

}
