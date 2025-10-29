/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller.security;

// Usage for API security
import com.spring5.secfilterconverter.HMACService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureApiController {

    @Autowired
    private HMACService hmacService;

    private final String API_SECRET = "your-secret-key";

    @PostMapping("/api/secure-data")
    public ResponseEntity<?> receiveSecureData(
            @RequestBody String payload,
            @RequestHeader("X-Signature") String signature) {

        try {
            if (hmacService.verifyHMAC(payload, signature, API_SECRET)) {
                // Process the secure data
                return ResponseEntity.ok("Data accepted");
            } else {
                return ResponseEntity.status(401).body("Invalid signature");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error verifying signature");
        }
    }
}
