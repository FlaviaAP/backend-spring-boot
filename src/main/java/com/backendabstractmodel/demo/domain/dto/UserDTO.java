package com.backendabstractmodel.demo.domain.dto;

import com.backendabstractmodel.demo.domain.enumeration.UserType;
import com.backendabstractmodel.demo.model.login.KeycloakUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserDTO implements BaseDTO<UUID> {

    protected UUID id;
    protected UserType userType;
    protected String name;
    protected String email;

    // keycloak
    protected String username;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected Set<String> roles;

    @Override
    public boolean isValidForSave() {
        return false;
    }

    public boolean isValidForBuildToken() {
        return Stream.of(id, getUsername()).allMatch(Objects::nonNull);
    }

    public KeycloakUser toKeycloakUser() {
        String[] nameParts = getName().split(" ");
        String firstname = nameParts[0];
        String lastname = String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length));

        return KeycloakUser.builder()
            .username(getUsername())
            .email(getEmail())
            .password(getPassword())
            .firstname(firstname)
            .lastname(lastname)
            .build();
    }

    public String getUsername() {
        return this.getEmail();
    }

}
