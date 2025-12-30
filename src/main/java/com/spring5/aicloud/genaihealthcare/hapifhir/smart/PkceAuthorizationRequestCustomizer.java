/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.stereotype.Component;

//@Component
public class PkceAuthorizationRequestCustomizer implements Consumer<OAuth2AuthorizationRequest.Builder> {

    @Autowired
    private PkceUtil pkceUtil;

    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder) {
        try {
            // Generate PKCE code verifier and challenge
            String codeVerifier = pkceUtil.generateCodeVerifier();
            String codeChallenge = pkceUtil.generateCodeChallenge(codeVerifier);

            // Add PKCE parameters to authorization request
            builder.attributes(attrs -> {
                        attrs.put(PkceParameterNames.CODE_VERIFIER, codeVerifier);
                    })
                    .additionalParameters(params -> {
                        params.put(PkceParameterNames.CODE_CHALLENGE, codeChallenge);
                        params.put(PkceParameterNames.CODE_CHALLENGE_METHOD, "S256");
                    });

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate PKCE parameters", e);
        }
    }
}
