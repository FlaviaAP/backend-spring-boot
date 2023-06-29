package com.backendabstractmodel.demo.services.config;

import com.backendabstractmodel.demo.enumeration.MessageEnum;
import com.backendabstractmodel.demo.exceptions.BusinessException;
import com.backendabstractmodel.demo.exceptions.ObjectNotFoundException;
import com.backendabstractmodel.demo.model.login.KeycloakUser;
import com.backendabstractmodel.demo.config.KeycloakProvider;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

@Service
public class KeycloakAdminClientService {

    private static final String UPDATE_PASSWORD_ACTION = "UPDATE_PASSWORD";

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientID;
    @Value("${keycloakClientIdFrontend}")
    private String clientIDFrontend;

    private final KeycloakProvider kcProvider;

    public KeycloakAdminClientService(KeycloakProvider keycloakProvider) {
        this.kcProvider = keycloakProvider;
    }

    private RealmResource getRealm() {
        return kcProvider.getUserManagerInstance().realm(realm);
    }

    private UsersResource getUsersResource() {
        return getRealm().users();
    }

    public void createKeycloakUserWithTemporaryPassword(KeycloakUser user, String compositeRole) {
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setEmail(user.getEmail());
        kcUser.setFirstName(user.getFirstname());
        kcUser.setLastName(user.getLastname());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        UsersResource usersResource = getUsersResource();
        Response response = usersResource.create(kcUser);
        checkCreateResponse(response);

        CredentialRepresentation credential = createTemporaryPasswordCredentials(user.getPassword());
        kcUser.setCredentials(Collections.singletonList(credential));
        kcUser.setRequiredActions(Arrays.asList(UPDATE_PASSWORD_ACTION));

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        UserResource userResource = usersResource.get(userId);
        userResource.update(kcUser);

        addCompositeRoleToUserAtBothClients(userResource, compositeRole);
    }

    private void checkCreateResponse(Response response) {
        int status = response.getStatus();

        if (status == HttpStatus.CONFLICT.value()) {
            throw new BusinessException(MessageEnum.ALREADY_EXISTS_REGISTER_WITH_SAME_USERNAME);
        }
        if (status != HttpStatus.CREATED.value()) {
            throw new ResponseStatusException(HttpStatus.valueOf(status), response.toString());
        }
    }

    private CredentialRepresentation createTemporaryPasswordCredentials(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(true);
        credential.setValue(password);
        return credential;
    }

    private void addCompositeRoleToUserAtBothClients(UserResource userResource, String role) {
        var realmResource = getRealm();

        for(String clientId : Arrays.asList(clientID, clientIDFrontend)) {
            ClientRepresentation client = realmResource.clients()
                .findByClientId(clientId).get(0);

            RoleRepresentation userClientRole = realmResource.clients()
                .get(client.getId())
                .roles().get(role).toRepresentation();

            userResource.roles()
                .clientLevel(client.getId())
                .add(Arrays.asList(userClientRole));
        }
    }


    public UserRepresentation getUser(String username, UsersResource usersResource) {
        return usersResource.search(username, true)
            .stream()
            .findFirst()
            .orElseThrow(ObjectNotFoundException::new);
    }


    public void setEmailAsVerified(String username) {
        UsersResource usersResource = getUsersResource();
        UserRepresentation kcUser = getUser(username, usersResource);
        if (kcUser.isEmailVerified()) {
            throw new BusinessException(MessageEnum.EMAIL_ALREADY_VERIFIED);
        }

        kcUser.setEmailVerified(true);

        UserResource userResource = usersResource.get(kcUser.getId());
        userResource.update(kcUser);
    }

    public void deleteUser(String username) {
        UsersResource usersResource = getUsersResource();
        UserRepresentation kcUser = getUser(username, usersResource);
        usersResource.get(kcUser.getId()).remove();
    }

}
