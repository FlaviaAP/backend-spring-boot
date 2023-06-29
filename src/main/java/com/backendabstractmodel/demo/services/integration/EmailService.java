package com.backendabstractmodel.demo.services.integration;

import com.backendabstractmodel.demo.domain.dto.UserDTO;
import com.backendabstractmodel.demo.domain.enumeration.TemplateTypeEnum;
import com.backendabstractmodel.demo.model.Email;
import com.backendabstractmodel.demo.services.TextTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${urlFront}")
    private String urlFront;

    @Value("${confirmEmailUrlSuffix}")
    private String confirmEmailUrlSuffix;

    @Value("${logoUrl}")
    private String logoUrl;

    @Autowired
    private EmailIntegrationService emailIntegrationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TextTemplateService textTemplateService;


    public void sendRegistrationConfirmEmail(UserDTO userDTO) {
        Map<String, Object> args = new HashMap<>();
        args.put("logoUrl", logoUrl);
        args.put("urlConfirm", buildRegistrationConfirmUrl(userDTO));
        args.put("name", userDTO.getName());
        args.put("tempPassword", userDTO.getPassword());

        Email email = Email.builder()
            .to(userDTO.getEmail())
            .subject("Confirmação de Cadastro")
            .args(args)
            .build();

        String template = textTemplateService.getByType(TemplateTypeEnum.CONFIRM_EMAIL).getContent();
        emailIntegrationService.send(email, template);
    }

    private String buildRegistrationConfirmUrl(UserDTO userDTO) {
        String token = tokenService.buildConfirmEmailToken(userDTO);
        return urlFront + confirmEmailUrlSuffix + token;
    }

}
