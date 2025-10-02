/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.analytics;

// package com.health.analytics.api
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class LoyaltyAnalyticsController {

	private final LoyaltyAnalyticsService service;

	public LoyaltyAnalyticsController(LoyaltyAnalyticsService service) {
		this.service = service;
	}

	@GetMapping("/loyalty-score/{patientId}")
	public Map<String, Object> getLoyaltyScore(@PathVariable String patientId) {
		return service.computeLoyaltyScore(patientId);
	}

}
