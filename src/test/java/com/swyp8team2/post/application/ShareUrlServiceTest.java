package com.swyp8team2.post.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ShareUrlServiceTest {

    ShareUrlService shareUrlService;

    @BeforeEach
    void setUp() throws Exception {
        shareUrlService = new ShareUrlService("asdfd", "1541235432");
    }

    @Test
    @DisplayName("암호화 및 복호화")
    void encryptAndDecrypt() {
        // given
        String plainText = "15411";

        // when
        String encryptedText = shareUrlService.encrypt(plainText);
        System.out.println("encryptedText = " + encryptedText);
        String decryptedText = shareUrlService.decrypt(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    @DisplayName("암호화 및 복호화 - 다른 키")
    void encryptAndDecrypt_differentKey() throws Exception {
        // given
        String plainText = "Hello, World!";
        ShareUrlService differentShareUrlService = new ShareUrlService("different", "234562");
        String encryptedText = differentShareUrlService.encrypt(plainText);

        // when then
        assertThatThrownBy(() -> shareUrlService.decrypt(encryptedText))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("복호화 - 이상한 토큰")
    void decrypt_invalidToken() {
        // given
        String invalid = "invalidToken";

        // when then
        assertThatThrownBy(() -> shareUrlService.decrypt(invalid))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("복호화 - empty string")
    void decrypt_emptyString() {
        // given
        String invalid = "";

        // when then
        assertThatThrownBy(() -> shareUrlService.decrypt(invalid))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }
}
