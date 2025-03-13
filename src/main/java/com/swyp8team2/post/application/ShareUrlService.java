package com.swyp8team2.post.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ShareUrlService {

    private final AesBytesEncryptor encryptor;

    public ShareUrlService(
            @Value("${crypto.secret-key.share-url}") String shareUrlSymmetricKey,
            @Value("${crypto.salt}") String salt
    ) {
        this.encryptor = new AesBytesEncryptor(shareUrlSymmetricKey, salt);
    }

    public String encrypt(String data) {
        try {
            byte[] encrypt = encryptor.encrypt(data.getBytes(StandardCharsets.UTF_8));
            return new String(Base62.createInstance().encode(encrypt), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.debug("encrypt error {}", e.getMessage());
            throw new BadRequestException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            if (!StringUtils.hasText(encryptedData)) {
                throw new InternalServerException(ErrorCode.INVALID_TOKEN);
            }
            byte[] decryptBytes = Base62.createInstance().decode(encryptedData.getBytes(StandardCharsets.UTF_8));
            byte[] decrypt = encryptor.decrypt(decryptBytes);
            return new String(decrypt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.debug("decrypt error {}", e.getMessage());
            throw new BadRequestException(ErrorCode.INVALID_TOKEN);
        }
    }
}
