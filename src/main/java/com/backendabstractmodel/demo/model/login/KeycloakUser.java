package com.backendabstractmodel.demo.model.login;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KeycloakUser {
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
}

