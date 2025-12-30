/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultOAuth2TokenRequestParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TokenManagementService {

    private final Map<String, OAuth2AuthorizedClient> authorizedClients
            = new ConcurrentHashMap<>();

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public void storeAuthorizedClient(OAuth2AuthorizedClient client,
            Authentication principal) {
        authorizedClientService.saveAuthorizedClient(client, principal);
    }

    public OAuth2AuthorizedClient getAuthorizedClient(String registrationId,
            Authentication principal) {
        return authorizedClientService.loadAuthorizedClient(registrationId, principal.getName());
    }

    public String refreshAccessToken(String registrationId, Authentication principal) {

        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(registrationId, principal);

        if (authorizedClient == null || authorizedClient.getRefreshToken() == null) {
            throw new RuntimeException("No refresh token available");
        }

        // Create refresh token request
        OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest
                = new OAuth2RefreshTokenGrantRequest(
                        clientRegistrationRepository.findByRegistrationId(registrationId),
                        authorizedClient.getAccessToken(),
                        authorizedClient.getRefreshToken()
                );

        // Execute refresh token request
        OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient = refreshTokenResponseClient();

        OAuth2AccessTokenResponse tokenResponse
                = refreshTokenResponseClient.getTokenResponse(refreshTokenGrantRequest);

        // Update authorized client
        OAuth2AuthorizedClient updatedAuthorizedClient = new OAuth2AuthorizedClient(
                authorizedClient.getClientRegistration(),
                authorizedClient.getPrincipalName(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );

        storeAuthorizedClient(updatedAuthorizedClient, principal);

        return tokenResponse.getAccessToken().getTokenValue();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient() {

        OAuth2AccessTokenResponseHttpMessageConverter tokenConverter
                = new OAuth2AccessTokenResponseHttpMessageConverter();

        RestClient restClient = restClientBuilder()
                .messageConverters(converters -> converters.add(tokenConverter))
                .build();

        return request -> restClient
                .post()
                .uri(request.getClientRegistration().getProviderDetails().getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(new DefaultOAuth2TokenRequestParametersConverter()
                        .convert(request))
                .retrieve()
                .body(OAuth2AccessTokenResponse.class);
    }

}
