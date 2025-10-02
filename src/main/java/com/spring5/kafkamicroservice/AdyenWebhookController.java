/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import com.adyen.model.notification.NotificationRequest;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks/adyen")
public class AdyenWebhookController {

	@PostMapping("/notifications")
	public ResponseEntity<String> handleNotification(@RequestBody String json) throws IOException {
		// Verify HMAC signature
		if (!verifySignature(json)) {
			return ResponseEntity.badRequest().build();
		}

		NotificationRequest notification = NotificationRequest.fromJson(json);
		notification.getNotificationItems().forEach(item -> {
			// Update payment status in DB
			// Publish event to Kafka if needed
		});

		return ResponseEntity.ok("[accepted]");
	}

	private boolean verifySignature(String json) {
		// Implement Adyen HMAC verification
		return true;
	}

}
