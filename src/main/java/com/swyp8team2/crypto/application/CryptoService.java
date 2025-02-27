package com.swyp8team2.crypto.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Slf4j
public class CryptoService {

    private static final String ALGORITHM = "AES";
    private final SecretKey secretKey;

    public CryptoService() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256);
        this.secretKey = keyGenerator.generateKey();
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.encodeBase64URLSafeString(encryptedBytes)
                    .replace('+', 'A')
                    .replace('/', 'B');
        } catch (Exception e) {
            log.error("encrypt error {}", e.getMessage());
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.decodeBase64(
                    encryptedData
                            .replace('A', '+')
                            .replace('B', '/')
            );
            return new String(cipher.doFinal(decoded));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.debug("decrypt error {}", e.getMessage());
            throw new BadRequestException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("decrypt error {}", e.getMessage());
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
