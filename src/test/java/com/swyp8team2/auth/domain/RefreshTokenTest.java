package com.swyp8team2.auth.domain;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RefreshTokenTest {

    @Test
    @DisplayName("Refresh Token 교체")
    void rotate() throws Exception {
        //given
        String token = "refreshToken";
        String newToken = "newRefreshToken";
        RefreshToken refreshToken = new RefreshToken(1L, token);

        //when
        refreshToken.rotate(token, newToken);

        //then
        assertThat(refreshToken.getToken()).isEqualTo(newToken);
    }

    @Test
    @DisplayName("Refresh Token 교체 - 현재 토큰과 다른 경우")
    void rotate_() throws Exception {
        //given
        String newToken = "newRefreshToken";
        RefreshToken refreshToken = new RefreshToken(1L, "refreshToken");

        //when then
        assertThatThrownBy(() -> refreshToken.rotate("mismatchToken", newToken))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_MISMATCHED.getMessage());
    }
}
