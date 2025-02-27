package com.swyp8team2.crypto.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@Slf4j
@Service
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
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("encrypt error {}", e.getMessage());
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.debug("decrypt error {}", e.getMessage());
            throw new BadRequestException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("decrypt error {}", e.getMessage());
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
