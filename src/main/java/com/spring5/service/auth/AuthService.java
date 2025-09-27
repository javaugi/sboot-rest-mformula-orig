/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service.auth;

import com.spring5.entity.auth.ApiClient;
import com.spring5.dto.auth.OAuth2UserInfo;
import com.spring5.entity.auth.ExternalUser;
import com.spring5.entity.auth.InternalUser;
import com.spring5.repository.auth.ApiClientRepository;
import com.spring5.repository.auth.ExternalUserRepository;
import com.spring5.repository.auth.InternalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class AuthService {

    private final InternalUserRepository internalUserRepository;
    private final ExternalUserRepository externalUserRepository;
    private final ApiClientRepository apiClientRepository;
    private final OAuth2Service oauth2Service;
    private final PasswordEncoder passwordEncoder;

    public AuthService(InternalUserRepository internalUserRepository,
        ExternalUserRepository externalUserRepository,
        ApiClientRepository apiClientRepository,
        OAuth2Service oauth2Service,
        PasswordEncoder passwordEncoder) {
        this.internalUserRepository = internalUserRepository;
        this.externalUserRepository = externalUserRepository;
        this.apiClientRepository = apiClientRepository;
        this.oauth2Service = oauth2Service;
        this.passwordEncoder = passwordEncoder;
    }

    public InternalUser authenticateInternalUser(String username, String password) {
        return internalUserRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElse(new InternalUser());
    }

    public ExternalUser authenticateExternalUser(String provider, String code) {
        OAuth2UserInfo userInfo = oauth2Service.getUserInfo(provider, code);

        return externalUserRepository.findByEmailAndProvider(userInfo.getEmail(), provider)
            .orElseGet(() -> createExternalUser(userInfo, provider));
    }

    public ApiClient authenticateApiClient(String apiKey) {
        return apiClientRepository.findByApiKey(apiKey)
            .filter(ApiClient::isActive)
            .orElse(ApiClient.builder().build());
    }

    public String getOAuth2RedirectUrl(String provider) {
        return oauth2Service.getAuthorizationUrl(provider);
    }

    private ExternalUser createExternalUser(OAuth2UserInfo userInfo, String provider) {
        ExternalUser user = ExternalUser.builder()
            .email(userInfo.getEmail())
            .name(userInfo.getName())
            .provider(provider)
            .providerId(userInfo.getId())
            .build();

        return externalUserRepository.save(user);
    }
}
