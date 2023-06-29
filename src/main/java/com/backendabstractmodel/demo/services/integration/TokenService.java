package com.backendabstractmodel.demo.services.integration;

import com.backendabstractmodel.demo.domain.dto.UserDTO;
import com.backendabstractmodel.demo.enumeration.MessageEnum;
import com.backendabstractmodel.demo.exceptions.BusinessException;
import com.backendabstractmodel.demo.services.util.CryptoUtilService;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class TokenService {

    private static final String TOKEN_CLAIM = "data";

    @Value("${tokenExpirationTimeMinutes}")
    private Integer tokenExpirationMinutes;

    @Value("${tokenSecret}")
    private String tokenSecret;

    @Value("${tokenCryptoSecret}")
    private String tokenCryptoSecret;

    @Value("${passwordCryptoSecret}")
    private String passwordCryptoSecret;

    @Autowired
    private CryptoUtilService cryptoUtilService;


    public String buildConfirmEmailToken(UserDTO userDTO) {
        if (!userDTO.isValidForBuildToken()) {
            throw new BusinessException(MessageEnum.MISSING_DATA_FOR_SAVE);
        }

        String password = userDTO.getPassword();
        if (Objects.nonNull(password)) {
            password = cryptoUtilService.encrypt(password, passwordCryptoSecret);
        }

        UserDTO userDTOToClaim = UserDTO.builder()
            .id(userDTO.getId())
            .email(userDTO.getEmail())
            .userType(userDTO.getUserType())
            .password(password)
            .build();

        String token = Jwts.builder()
            .claim(TOKEN_CLAIM, userDTOToClaim)
            .setSubject(userDTO.getEmail())
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(getNow())
            .setExpiration( Date.from(Instant.now().plus(tokenExpirationMinutes, ChronoUnit.MINUTES)) )
            .signWith(SignatureAlgorithm.HS512, tokenSecret)
            .compact();

        return cryptoUtilService.encryptUrl(token, tokenCryptoSecret);
    }

    public UserDTO extractUserFromToken(String token) {
        token = cryptoUtilService.decryptUrl(token, tokenCryptoSecret);
        try {
            var claims = Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(token)
                .getBody();

            var gson = new GsonBuilder().create();
            String jsonString = gson.toJson(claims.get(TOKEN_CLAIM), Map.class);
            UserDTO userDTO = gson.fromJson(jsonString, UserDTO.class);

            validateTokenContent(claims.getSubject(), userDTO);

            String password = userDTO.getPassword();
            if (Objects.nonNull(password)) {
                userDTO.setPassword( cryptoUtilService.decrypt(password, passwordCryptoSecret) );
            }

            return userDTO;
        }
        catch (BusinessException e) {
            throw e;
        }
        catch (ExpiredJwtException e) {
            throw new BusinessException(MessageEnum.EXPIRED_URL);
        }
        catch (Exception e) {
            throw new BusinessException(MessageEnum.INVALID_URL);
        }
    }

    private void validateTokenContent(String subject, UserDTO userDTO) {
        boolean missingAnyData = Stream.of(
            subject,
            userDTO.getId(),
            userDTO.getEmail(),
            userDTO.getUserType()
        ).anyMatch(Objects::isNull);

        if (missingAnyData || !subject.equals(userDTO.getEmail())) {
            throw new BusinessException(MessageEnum.INVALID_URL);
        }
    }

    private Date getNow() {
        return Date.from(Instant.now());
    }

}
