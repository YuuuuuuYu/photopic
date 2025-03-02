package com.swyp8team2.auth.application;

import com.swyp8team2.auth.application.jwt.JwtClaim;
import com.swyp8team2.auth.application.jwt.JwtProvider;
import com.swyp8team2.auth.application.jwt.JwtService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.auth.domain.RefreshToken;
import com.swyp8team2.auth.domain.RefreshTokenRepository;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

class JwtServiceTest extends IntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    JwtProvider jwtProvider;

    @Test
    @DisplayName("새 토큰 발급하고 refresh token을 db에 저장해야 함")
    void createToken() throws Exception {
        //given
        long givenUserId = 1L;
        TokenPair expectedTokenPair = new TokenPair("accessToken", "refreshToken");
        given(jwtProvider.createToken(any(JwtClaim.class)))
                .willReturn(expectedTokenPair);

        //when
        TokenResponse tokenResponse = jwtService.createToken(givenUserId);

        //then
        TokenPair tokenPair = tokenResponse.tokenPair();
        RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(givenUserId).get();
        assertThat(tokenPair).isEqualTo(expectedTokenPair);
        assertThat(findRefreshToken.getToken()).isEqualTo(expectedTokenPair.refreshToken());
    }

    @Test
    @DisplayName("토큰 재발급하고 refresh token을 db에 갱신해야 함")
    void reissue() throws Exception {
        //given
        long givenUserId = 1L;
        String givenRefreshToken = "refreshToken";
        String newRefreshToken = "newRefreshToken";
        TokenPair expectedTokenPair = new TokenPair("newAccessToken", newRefreshToken);
        given(jwtProvider.parseToken(any(String.class)))
                .willReturn(new JwtClaim(givenUserId));
        given(jwtProvider.createToken(any(JwtClaim.class)))
                .willReturn(expectedTokenPair);
        refreshTokenRepository.save(new RefreshToken(givenUserId, givenRefreshToken));

        //when
        TokenResponse tokenResponse = jwtService.reissue(givenRefreshToken);

        //then
        TokenPair tokenPair = tokenResponse.tokenPair();
        RefreshToken findRefreshToken = refreshTokenRepository.findByUserId(givenUserId).get();
        assertThat(tokenPair).isEqualTo(expectedTokenPair);
        assertThat(findRefreshToken.getToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("토큰 재발급 - refresh token이 존재하지 않는 경우")
    void reissue_refreshTokenNotFound() throws Exception {
        //given
        given(jwtProvider.parseToken(any(String.class)))
                .willReturn(new JwtClaim(1L));

        //when
        assertThatThrownBy(() -> jwtService.reissue("refreshToken"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 - refresh token이 일치하지 않은 경우")
    void reissue_refreshTokenMismatched() throws Exception {
        //given
        long givenUserId = 1L;
        String givenRefreshToken = "refreshToken";
        given(jwtProvider.parseToken(any(String.class)))
                .willReturn(new JwtClaim(givenUserId));
        given(jwtProvider.createToken(any(JwtClaim.class)))
                .willReturn(new TokenPair("accessToken", "newRefreshToken"));
        refreshTokenRepository.save(new RefreshToken(givenUserId, givenRefreshToken));

        //when
        assertThatThrownBy(() -> jwtService.reissue("mismatchToken"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_MISMATCHED.getMessage());
    }

    @Test
    @DisplayName("로그아웃하면 refresh token을 db에서 삭제해야 함")
    void signOut() throws Exception {
        //given
        long givenUserId = 1L;
        String givenRefreshToken = "refreshToken";
        given(jwtProvider.parseToken(eq(givenRefreshToken)))
                .willReturn(new JwtClaim(givenUserId));
        refreshTokenRepository.save(new RefreshToken(givenUserId, givenRefreshToken));

        //when
        jwtService.signOut(givenRefreshToken);

        //then
        assertThat(refreshTokenRepository.findByUserId(givenUserId)).isEmpty();
    }
}
