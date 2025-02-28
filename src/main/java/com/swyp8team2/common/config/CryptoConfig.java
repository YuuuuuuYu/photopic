package com.swyp8team2.common.config;

import com.swyp8team2.common.annotation.GuestTokenCryptoService;
import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.common.annotation.ShareUrlCryptoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Configuration
public class CryptoConfig {

    private final String guestTokenSymmetricKey;
    private final String shareUrlSymmetricKey;
    private final String salt;

    public CryptoConfig(
            @Value("${crypto.secret-key.guest-token}") String guestTokenSymmetricKey,
            @Value("${crypto.secret-key.share-url}") String shareUrlSymmetricKey,
            @Value("${crypto.salt}") String salt
    ) {
        this.guestTokenSymmetricKey = guestTokenSymmetricKey;
        this.shareUrlSymmetricKey = shareUrlSymmetricKey;
        this.salt = salt;
    }

    @GuestTokenCryptoService
    @Bean(name = GuestTokenCryptoService.QUALIFIER)
    public CryptoService guestTokenCryptoService() throws Exception {
        return new CryptoService(new AesBytesEncryptor(guestTokenSymmetricKey, salt));
    }

    @ShareUrlCryptoService
    @Bean(name = ShareUrlCryptoService.QUALIFIER)
    public CryptoService shareUrlCryptoService() throws Exception {
        return new CryptoService(new AesBytesEncryptor(shareUrlSymmetricKey, salt));
    }
}
