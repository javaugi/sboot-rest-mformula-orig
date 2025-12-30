/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private final OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter
            = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest request) {

        RequestEntity<?> entity = defaultConverter.convert(request);

        if (entity == null) {
            return null;
        }

        // Extract code verifier from authorization request attributes
        Map<String, Object> attributes = request.getAuthorizationExchange().getAuthorizationRequest().getAdditionalParameters();
        String codeVerifier = (String) attributes.get(PkceParameterNames.CODE_VERIFIER);

        if (codeVerifier != null) {
            // Add code_verifier to token request body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            if (entity.getBody() instanceof MultiValueMap) {
                body = (MultiValueMap<String, String>) entity.getBody();
            } else if (entity.getBody() instanceof Map) {
                Map<String, String> map = (Map<String, String>) entity.getBody();
                body.setAll(map);
            }

            body.add(PkceParameterNames.CODE_VERIFIER, codeVerifier);

            // Recreate request entity with updated body
            return new RequestEntity<>(
                    body,
                    entity.getHeaders(),
                    entity.getMethod(),
                    entity.getUrl(),
                    entity.getType()
            );
        }

        return entity;
    }
}
