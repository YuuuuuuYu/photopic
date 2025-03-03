package com.swyp8team2.common.config;

import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.common.annotation.ShareUrlCryptoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Configuration
public class CryptoConfig {

    private final String shareUrlSymmetricKey;
    private final String salt;

    public CryptoConfig(
            @Value("${crypto.secret-key.share-url}") String shareUrlSymmetricKey,
            @Value("${crypto.salt}") String salt
    ) {
        this.shareUrlSymmetricKey = shareUrlSymmetricKey;
        this.salt = salt;
    }

    @ShareUrlCryptoService
    @Bean(name = ShareUrlCryptoService.QUALIFIER)
    public CryptoService shareUrlCryptoService() throws Exception {
        return new CryptoService(new AesBytesEncryptor(shareUrlSymmetricKey, salt));
    }
}
