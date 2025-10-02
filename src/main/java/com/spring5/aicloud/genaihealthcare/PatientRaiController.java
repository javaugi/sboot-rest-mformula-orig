/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aiapi/patients")
class PatientRaiController {

	private final LLMService llmService;

	private final ResponsibleAIService responsibleAiService;

	// Use constructor injection for dependencies
	@Autowired
	public PatientRaiController(LLMService llmService, ResponsibleAIService responsibleAiService) {
		this.llmService = llmService;
		this.responsibleAiService = responsibleAiService;
	}

	/**
	 * Endpoint for a patient to submit a query and get a response. This endpoint
	 * orchestrates the entire process: privacy checks, LLM call, logging, and index
	 * updates.
	 */
	@PostMapping("/inquire")
	public ResponseEntity<PatientInquiryResponse> handleInquiry(@RequestBody PatientInquiryRequest request) {
		// 1. Human-centered design: Start with an empathetic human-in-the-loop mindset.
		// No AI interaction occurs until we've confirmed safety and privacy.
		responsibleAiService.performPrivacyCheck(request);

		// 2. Use the LLM to generate a personalized and helpful response.
		PatientInquiryResponse response = llmService.getPersonalizedResponse(request);

		// 3. AI Governance & Trust: Log the interaction and update the AI Index.
		responsibleAiService.logAiInteraction(request, response);
		responsibleAiService.updateResponsibleAiIndex(response);

		// 4. Return the generated response.
		// The front-end would use the 'requiresHumanReview' flag to show
		// a disclaimer and inform the patient that a doctor will follow up.
		// The loyalty offer would also be displayed.
		return ResponseEntity.ok(response);
	}

}
