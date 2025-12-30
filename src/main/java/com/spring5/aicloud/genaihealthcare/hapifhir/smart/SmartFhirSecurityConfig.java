/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.hapifhir.smart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

//@Configuration
//@EnableWebSecurity
public class SmartFhirSecurityConfig {
    @Autowired
    private PkceAuthorizationRequestCustomizer pkceAuthorizationRequestCustomizer;

    @Autowired
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/**".split(" "))
                )
                .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/oauth2/**", "/error").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .authorizationEndpoint(authorization -> authorization
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(
                        cookieAuthorizationRequestRepository()
                )
                //.authorizationRequestResolver(authorizationRequestResolver())
                )
                .redirectionEndpoint(redirection -> redirection
                .baseUri("/login/oauth2/code/*")
                )
                .tokenEndpoint(token -> token
                .accessTokenResponseClient(accessTokenResponseClient())
                )
                .userInfoEndpoint(userInfo -> userInfo
                .userService(oauth2UserService)
                )
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                )
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest>
            cookieAuthorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver resolver
                = new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorize"
                );

        // Apply PKCE customization
        resolver.setAuthorizationRequestCustomizer(pkceAuthorizationRequestCustomizer);

        return resolver;
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {

        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient
                = new DefaultAuthorizationCodeTokenResponseClient();

        // Custom converter to handle PKCE
        tokenResponseClient.setRequestEntityConverter(
                new CustomRequestEntityConverter()
        );

        return tokenResponseClient;
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return (userRequest) -> {
            OAuth2User user = delegate.loadUser(userRequest);

            // Extract additional claims from ID token
            Map<String, Object> additionalAttributes
                    = extractAdditionalAttributes(userRequest);

            // Merge attributes
            Map<String, Object> combinedAttributes = new HashMap<>(user.getAttributes());
            combinedAttributes.putAll(additionalAttributes);

            return new DefaultOAuth2User(
                    user.getAuthorities(),
                    combinedAttributes,
                    userRequest.getClientRegistration()
                            .getProviderDetails()
                            .getUserInfoEndpoint()
                            .getUserNameAttributeName()
            );
        };
    }

    private Map<String, Object> extractAdditionalAttributes(OAuth2UserRequest userRequest) {
        Map<String, Object> attributes = new HashMap<>();

        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        Map<String, Object> idTokenClaims = parseIdToken(accessToken.getTokenValue());
        attributes.putAll(idTokenClaims);

        return attributes;
    }

    private Map<String, Object> parseIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            String payload = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            return new ObjectMapper().readValue(payload, new TypeReference<Map<String, Object>>() {
            });

        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }
}
