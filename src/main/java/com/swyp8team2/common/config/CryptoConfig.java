package com.swyp8team2.common.config;

import com.swyp8team2.common.annotation.GuestTokenCryptoService;
import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.common.annotation.ShareUrlCryptoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

    @GuestTokenCryptoService
    @Bean(name = GuestTokenCryptoService.QUALIFIER)
    public CryptoService guestTokenCryptoService() throws Exception {
        return new CryptoService();
    }

    @ShareUrlCryptoService
    @Bean(name = ShareUrlCryptoService.QUALIFIER)
    public CryptoService shareUrlCryptoService() throws Exception {
        return new CryptoService();
    }
}
