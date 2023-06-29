package com.backendabstractmodel.demo.services.util;

import com.backendabstractmodel.demo.exceptions.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Service
public class CryptoUtilService {

    private static final String ALGORITHM = "AES";

    private SecretKeySpec secretKey;

    private void prepareSecretKey(String myKey) throws NoSuchAlgorithmException {
        byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, ALGORITHM);
    }


    public String encrypt(String strToEncrypt, String secret) {
        return encrypt(strToEncrypt, secret, Base64.getEncoder());
    }

    public String encryptUrl(String strToEncrypt, String secret) {
        return encrypt(strToEncrypt, secret, Base64.getUrlEncoder());
    }

    private String encrypt(String strToEncrypt, String secret, Base64.Encoder base64Encoder) {
        try {
            prepareSecretKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return base64Encoder.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e) {
            log.error("Error while encrypting");
            throw new UnexpectedException(e);
        }
    }


    public String decrypt(String strToDecrypt, String secret) {
        return decrypt(strToDecrypt, secret, Base64.getDecoder());
    }

    public String decryptUrl(String strToDecrypt, String secret) {
        return decrypt(strToDecrypt, secret, Base64.getUrlDecoder());
    }

    private String decrypt(String strToDecrypt, String secret, Base64.Decoder base64Decoder) {
        try {
            prepareSecretKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(base64Decoder.decode(strToDecrypt)));
        }
        catch (Exception e) {
            log.error("Error while decrypting");
            throw new UnexpectedException(e);
        }
    }

}
