package com.swyp8team2.crypto.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {

    CryptoService cryptoService;

    @BeforeEach
    void setUp() throws Exception {
        cryptoService = new CryptoService(new AesBytesEncryptor("asdfd", "1541235432"));
    }

    @Test
    @DisplayName("암호화 및 복호화")
    void encryptAndDecrypt() {
        // given
        String plainText = "15411";

        // when
        String encryptedText = cryptoService.encrypt(plainText);
        System.out.println("encryptedText = " + encryptedText);
        String decryptedText = cryptoService.decrypt(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    @DisplayName("암호화 및 복호화 - 다른 키")
    void encryptAndDecrypt_differentKey() throws Exception {
        // given
        String plainText = "Hello, World!";
        CryptoService differentCryptoService = new CryptoService(new AesBytesEncryptor("different", "234562"));
        String encryptedText = differentCryptoService.encrypt(plainText);

        // when then
        assertThatThrownBy(() -> cryptoService.decrypt(encryptedText))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("복호화 - 이상한 토큰")
    void decrypt_invalidToken() {
        // given
        String invalid = "invalidToken";

        // when then
        assertThatThrownBy(() -> cryptoService.decrypt(invalid))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }
}
