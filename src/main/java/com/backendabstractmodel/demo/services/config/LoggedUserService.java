package com.backendabstractmodel.demo.services.config;

import com.backendabstractmodel.demo.domain.dto.UserDTO;
import com.backendabstractmodel.demo.enumeration.MessageEnum;
import com.backendabstractmodel.demo.resources.util.RoleControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoggedUserService {

    private final KeycloakUtilService keycloakUtilService;


    public UserDTO getLoggedUser() {
        return keycloakUtilService.getLoggedUser();
    }

    public String getUsername() {
        return getLoggedUser().getUsername();
    }

    public boolean hasRole(String role) {
        var roles = keycloakUtilService.getKeycloakAccount().getRoles();
        return roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(RoleControl.ADMIN);
    }

    public boolean isEmployed() {
        return hasRole(RoleControl.EMPLOYED);
    }

    public boolean isCustomer() {
        return hasRole(RoleControl.CUSTOMER);
    }


    private AccessDeniedException getAccessDeniedException() {
        return new AccessDeniedException(MessageEnum.ACCESS_DENIED.toString());
    }

}
