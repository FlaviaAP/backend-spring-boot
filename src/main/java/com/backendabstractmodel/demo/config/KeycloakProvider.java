package com.backendabstractmodel.demo.config;

import lombok.Getter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KeycloakProvider {

    @Value("${keycloak.auth-server-url}")
    public String serverURL;

    @Value("${keycloak.realm}")
    public String realm;

    @Value("${keycloak.resource}")
    public String clientID;

    @Value("${keycloak.credentials.secret}")
    public String clientSecret;

    @Value("${keycloakUserManagerUsername}")
    public String userManagerUsername;

    @Value("${keycloakUserManagerPassword}")
    public String userManagerPassword;

    public KeycloakProvider() {}

    public Keycloak getInstance() {
        return KeycloakBuilder.builder()
            .realm(realm)
            .serverUrl(serverURL)
            .clientId(clientID)
            .clientSecret(clientSecret)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .build();
    }

    public Keycloak getUserManagerInstance() {
        return KeycloakBuilder.builder()
            .realm(realm)
            .serverUrl(serverURL)
            .clientId(clientID)
            .clientSecret(clientSecret)
            .username(userManagerUsername)
            .password(userManagerPassword)
            .grantType(OAuth2Constants.PASSWORD)
            .build();
    }

}
