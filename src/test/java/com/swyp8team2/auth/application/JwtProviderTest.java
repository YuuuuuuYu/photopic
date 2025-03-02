package com.swyp8team2.auth.application;

import com.swyp8team2.auth.application.jwt.JwtClaim;
import com.swyp8team2.auth.application.jwt.JwtProvider;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

class JwtProviderTest {

    @Test
    @DisplayName("jwt 토큰 생성")
    void createToken() throws Exception {
        //given
        JwtProvider jwtProvider = new JwtProvider("2345asdfasdfsadfsdf243dfdsfsfssasdf", "issuer", Clock.systemDefaultZone());
        JwtClaim givenClaim = new JwtClaim(1L);

        //when
        TokenPair tokenPair = jwtProvider.createToken(givenClaim);
        JwtClaim jwtClaim = jwtProvider.parseToken(tokenPair.accessToken());

        //then
        assertAll(
                () -> assertThat(tokenPair.accessToken()).isNotNull(),
                () -> assertThat(tokenPair.refreshToken()).isNotNull(),
                () -> assertThat(jwtClaim.idAsLong()).isEqualTo(givenClaim.idAsLong())
        );
    }

    @Test
    @DisplayName("jwt 토큰 생성 - 만료 토큰")
    void parseClaim_expiredToken() throws Exception {
        //given
        Clock clock = Clock.systemDefaultZone();
        Clock mockedClocked = spy(clock);
        JwtProvider jwtProvider = new JwtProvider("2345asdfasdfsadfsdf243dfdsfsfssasdf", "issuer", mockedClocked);
        given(mockedClocked.instant())
                .willReturn(clock.instant().minus(24, ChronoUnit.HOURS));

        //when then
        TokenPair tokenPair = jwtProvider.createToken(new JwtClaim(1L));
        assertThatThrownBy(() -> jwtProvider.parseToken(tokenPair.accessToken()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.EXPIRED_TOKEN.getMessage());
    }

    @Test
    @DisplayName("jwt 토큰 생성 - key가 다른 경우")
    void parseClaim_differentKey() throws Exception {
        //given
        Clock clock = Clock.systemDefaultZone();
        JwtProvider jwtProvider = new JwtProvider("2345asdfasdfsadfsdf243dfdsfsfssasdf", "issuer", clock);
        JwtProvider jwtProviderWithDifferentKey = new JwtProvider("1211qwerqwerqwer1111qwerqwerqwer", "issuer", clock);

        //when then
        TokenPair tokenPair = jwtProviderWithDifferentKey.createToken(new JwtClaim(1L));
        assertThatThrownBy(() -> jwtProvider.parseToken(tokenPair.accessToken()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("jwt 토큰 생성 - issuer가 다른 경우")
    void parseClaim_differentIssuer() throws Exception {
        //given
        Clock clock = Clock.systemDefaultZone();
        JwtProvider jwtProvider = new JwtProvider("2345asdfasdfsadfsdf243dfdsfsfssasdf", "issuer", clock);
        JwtProvider jwtProviderWithDifferentIssuer = new JwtProvider("2345asdfasdfsadfsdf243dfdsfsfssasdf", "asdf", clock);

        //when then
        TokenPair tokenPair = jwtProviderWithDifferentIssuer.createToken(new JwtClaim(1L));
        assertThatThrownBy(() -> jwtProvider.parseToken(tokenPair.accessToken()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }
}
