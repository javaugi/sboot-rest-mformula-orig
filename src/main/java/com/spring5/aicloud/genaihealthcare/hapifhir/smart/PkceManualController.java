/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/pkce")
public class PkceManualController {

    @Autowired
    private PkceUtil pkceUtil;

    @GetMapping("/initiate")
    public ResponseEntity<String> initiatePkceFlow() throws Exception {
        String state = generateRandomString(32);
        String codeVerifier = pkceUtil.generateCodeVerifier();
        String codeChallenge = pkceUtil.generateCodeChallenge(codeVerifier);
        String nonce = generateRandomString(32);

        // Store code_verifier in session or database
        HttpSession session = SessionUtil.getSession();
        session.setAttribute("code_verifier", codeVerifier);
        session.setAttribute("state", state);
        session.setAttribute("nonce", nonce);

        // Build authorization URL manually
        String authorizationUrl = UriComponentsBuilder
                .fromHttpUrl("https://your-auth-server.com/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "your-client-id")
                .queryParam("redirect_uri", "http://localhost:8080/pkce/callback")
                .queryParam("scope", "openid profile email")
                .queryParam("state", state)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .queryParam("nonce", nonce)
                .build()
                .toUriString();

        return ResponseEntity.ok("{\"authorization_url\": \"" + authorizationUrl + "\"}");
    }

    @GetMapping("/callback")
    public String handleCallback(
            @RequestParam("code") String authorizationCode,
            @RequestParam("state") String state,
            HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();
        String storedState = (String) session.getAttribute("state");
        String codeVerifier = (String) session.getAttribute("code_verifier");

        // Validate state
        if (!state.equals(storedState)) {
            throw new RuntimeException("Invalid state parameter");
        }

        // Exchange authorization code for tokens
        String tokenResponse = exchangeCodeForTokens(authorizationCode, codeVerifier);

        // Process token response
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> tokenData = mapper.readValue(tokenResponse, Map.class);

        // Store tokens in session
        session.setAttribute("access_token", tokenData.get("access_token"));
        session.setAttribute("id_token", tokenData.get("id_token"));
        session.setAttribute("refresh_token", tokenData.get("refresh_token"));

        return "redirect:/dashboard";
    }

    private String exchangeCodeForTokens(String authorizationCode, String codeVerifier)
            throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("your-client-id", "your-client-secret");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);
        body.add("redirect_uri", "http://localhost:8080/pkce/callback");
        body.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> request
                = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://your-auth-server.com/oauth2/token",
                request,
                String.class
        );

        return response.getBody();
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
