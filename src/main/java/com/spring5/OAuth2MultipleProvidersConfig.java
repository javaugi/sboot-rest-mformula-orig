/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
@Configuration
// */
public class OAuth2MultipleProvidersConfig {
    //org.springframework.security.config.annotation.web.configuration.OAuth2ClientConfiguration$OAuth2AuthorizedClientManagerRegistrar
    
    /*
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("my-client")
            .clientId("your-client-id")
            .clientSecret("your-client-secret")
            .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email")
            .authorizationUri("https://your-auth-server.com/oauth2/authorize")
            .tokenUri("https://your-auth-server.com/oauth2/token")
            .userInfoUri("https://your-auth-server.com/userinfo")
            .userNameAttributeName("sub")
            .clientName("My Client App")
            .build();

        return new InMemoryClientRegistrationRepository(clientRegistration);
    }
    
    @Bean 
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }
        
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider
                = OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        //.clientCredentials()
                        //.password()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager
                = new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
    
    // */
}

/*
OAuth 2.0 is an open standard for authorization that allows a user to grant an application access to their data on a server (e.g., Google Drive, Twitter)
    without sharing their password. Essentially, it enables third-party applications to access user data securely by delegating authentication to the service provider. 
Here's a more detailed breakdown:
Key Concepts:
Authorization:
.
The process of granting a third-party application permission to access a user's data on a service provider's server. 
Authentication:
.
The process of verifying a user's identity. 
Service Provider:
.
The service (like Google, Facebook, etc.) that hosts the user's data and handles authentication. 
Third-party Application:
.
An application (like a mobile app, web app, etc.) that needs to access user data on a service provider's server. 
Access Token:
.
A short-lived token that grants the application access to specific user data. 
Refresh Token:
.
A longer-lived token that can be used to obtain new access tokens when the current one expires. 
How it Works:
The application requests authorization from the service provider on behalf of the user. 
The user is redirected to the service provider's login page to authenticate (enter username and password). 
Once the user is authenticated, they grant the application permission to access their data. 
The service provider issues an access token to the application. 
The application uses the access token to access the user's data on the service provider's server. 
Benefits:
Security: Users don't have to share their passwords with every application. 
Privacy: Users can control what data they share with which applications. 
Simplified User Experience: Users can easily grant access to multiple applications without having to re-enter their credentials. 
Improved Developer Experience: Developers can integrate OAuth 2.0 into their applications more easily. 
*/