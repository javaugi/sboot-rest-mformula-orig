/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aiapi/ai-governance")
public class AIGovernanceController {

	@Autowired
	private ResponsibleAIIndexService aiService;

	@PostMapping("/index")
	public ResponseEntity<ResponsibleAIIndex> createAIIndex(@RequestBody ResponsibleAIIndex index) {
		return ResponseEntity.ok(aiService.saveOrUpdateIndex(index));
	}

	@GetMapping("/compliant-models")
	public ResponseEntity<List<ResponsibleAIIndex>> getCompliantModels() {
		return ResponseEntity.ok(aiService.getAllCompliantModels());
	}

	@GetMapping("/industry-average")
	public ResponseEntity<Double> getIndustryAverage() {
		return ResponseEntity.ok(aiService.getIndustryAverageScore());
	}

	@GetMapping("/model/{modelName}")
	public ResponseEntity<ResponsibleAIIndex> getModelDetails(@PathVariable String modelName) {
		ResponsibleAIIndex model = aiService.getModelByName(modelName);
		if (model != null) {
			return ResponseEntity.ok(model);
		}
		return ResponseEntity.notFound().build();
	}

}
