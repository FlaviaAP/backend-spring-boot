package com.backendabstractmodel.demo.services.config;

import com.backendabstractmodel.demo.config.KeycloakProvider;
import com.backendabstractmodel.demo.domain.dto.UserDTO;
import com.backendabstractmodel.demo.enumeration.MessageEnum;
import com.backendabstractmodel.demo.exceptions.UnexpectedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

@Service
public class KeycloakUtilService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakUtilService.class);

    @Autowired
    private KeycloakAdminClientService keycloakAdminClientService;

    @Autowired
    private KeycloakProvider keycloakProvider;


    public void createUser(UserDTO userDTO, String compositeRole) {
        String username = null;
        try {
            username = userDTO.getUsername();
            var keycloakUser = userDTO.toKeycloakUser();
            keycloakAdminClientService.createKeycloakUserWithTemporaryPassword(keycloakUser, compositeRole);
        } catch (Exception e) {
            LOGGER.error("Error at create keycloak user {} with role {}", username, compositeRole);
            throw new UnexpectedException(MessageEnum.KEYCLOAK_CREATE_USER_ERROR, e);
        }
    }

    public void setUserEmailVerified(String username) {
        keycloakAdminClientService.setEmailAsVerified(username);
    }

    public void deleteUser(String username) {
        keycloakAdminClientService.deleteUser(username);
    }


    /* Logged user*/

    public SimpleKeycloakAccount getKeycloakAccount() {
        return (SimpleKeycloakAccount) SecurityContextHolder.getContext()
            .getAuthentication().getDetails();
    }

    public UserDTO getLoggedUser() {
        SimpleKeycloakAccount account = getKeycloakAccount();
        JsonNode jsonNode = getLoggedUserJson(account);
        return jsonNodeToUser(jsonNode, account.getRoles());
    }

    private JsonNode getLoggedUserJson(SimpleKeycloakAccount account) {
        try {
            RefreshableKeycloakSecurityContext context = account.getKeycloakSecurityContext();

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(context.getDeployment().getAccountUrl());
            request.addHeader("accept", "application/json");
            request.addHeader("Authorization", "Bearer " + context.getTokenString());
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();
            if (status != Response.Status.OK.getStatusCode()) {
                throw new ResponseStatusException(HttpStatus.valueOf(status), response.toString());
            }

            String json = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            return new ObjectMapper().readTree(json);
        }
        catch (IOException e) {
            throw new UnexpectedException("Error at convert JSON of logged user", e);
        }
    }

    private UserDTO jsonNodeToUser(JsonNode node, final Set<String> roles) {
        String firstName = node.get("firstName").textValue();
        String lastName = node.get("lastName").textValue();
        String fullName = firstName.concat(" ").concat(lastName);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(node.get("username").textValue());
        userDTO.setEmail(node.get("email").textValue());
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setName(fullName);
        userDTO.setRoles(roles);
        return userDTO;
    }

}
