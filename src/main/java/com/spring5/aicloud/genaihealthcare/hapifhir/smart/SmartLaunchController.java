/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/launch")
public class SmartLaunchController {

    @GetMapping("/ehr")
    public String launchFromEhr(
            @RequestParam(name = "iss", required = false) String fhirServerUrl,
            @RequestParam(name = "launch", required = false) String launchToken,
            HttpServletRequest request) {

        // Store launch context in session
        request.getSession().setAttribute("fhirServerUrl", fhirServerUrl);
        request.getSession().setAttribute("launchToken", launchToken);

        // Build OAuth2 authorization URL with SMART parameters
        String redirectUrl = buildAuthorizationUrl(fhirServerUrl, launchToken);

        return "redirect:" + redirectUrl;
    }

    private String buildAuthorizationUrl(String fhirServerUrl, String launchToken) {
        // Discover SMART endpoints from FHIR server's metadata
        // This is simplified - in production, you'd fetch from /.well-known/smart-configuration

        return "https://fhir.epic.com/interconnect-fhir-oauth/oauth2/authorize"
                + "?response_type=code"
                + "&client_id=your-client-id"
                + "&redirect_uri=http://localhost:8080/login/oauth2/code/epic"
                + "&scope=openid%20fhirUser%20launch%20patient/Observation.read%20patient/Patient.read"
                + "&state=" + UUID.randomUUID().toString()
                + "&aud=" + URLEncoder.encode(fhirServerUrl, StandardCharsets.UTF_8)
                + "&launch=" + launchToken;
    }
}
