package com.swyp8team2.auth.presentation.filter;

import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HeaderTokenExtractorTest {

    HeaderTokenExtractor tokenExtractor;

    @BeforeEach
    void setUp() {
        tokenExtractor = new HeaderTokenExtractor();
    }

    @Test
    @DisplayName("토큰을 추출해야 함")
    void extractToken() throws Exception {
        //given
        String authorization = "Bearer token";

        //when
        String token = tokenExtractor.extractToken(authorization);

        //then
        assertThat(token).isEqualTo("token");
    }

    @Test
    @DisplayName("헤더가 null일 경우 예외가 발생해야 함")
    void extractToken_null() throws Exception {
        //given
        String authorization = null;

        //when then
        assertThatThrownBy(() -> tokenExtractor.extractToken(authorization))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.INVALID_AUTH_HEADER.getMessage());
    }

    @Test
    @DisplayName("헤더 형식이 잘못되었을 경우 예외가 발생해야 함")
    void extractToken_notBearer() throws Exception {
        //given
        String authorization = "bearer token";

        //when then
        assertThatThrownBy(() -> tokenExtractor.extractToken(authorization))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.INVALID_AUTH_HEADER.getMessage());
    }

}
