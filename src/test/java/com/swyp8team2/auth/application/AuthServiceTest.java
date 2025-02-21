package com.swyp8team2.auth.application;

import com.swyp8team2.auth.application.jwt.JwtProvider;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.auth.application.oauth.OAuthService;
import com.swyp8team2.auth.application.oauth.dto.OAuthUserInfo;
import com.swyp8team2.auth.domain.Provider;
import com.swyp8team2.auth.domain.SocialAccount;
import com.swyp8team2.auth.domain.SocialAccountRepository;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class AuthServiceTest extends IntegrationTest {

    @Autowired
    AuthService authService;

    @MockitoBean
    OAuthService oAuthService;

    @MockitoBean
    JwtProvider jwtProvider;

    @Autowired
    SocialAccountRepository socialAccountRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("OAuth 로그인하면 토큰 발급해야 하고 유저 정보 없는 경우 유저 생성")
    void oAuthSignIn() throws Exception {
        //given
        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo("socialId", "profileImageUrl", "nickname", Provider.KAKAO);
        given(oAuthService.getUserInfo(anyString()))
                .willReturn(oAuthUserInfo);
        TokenPair expectedTokenPair = new TokenPair("accessToken", "refreshToken");
        given(jwtProvider.createToken(any()))
                .willReturn(expectedTokenPair);

        //when
        TokenPair tokenPair = authService.oauthSignIn("code");

        //then
        SocialAccount socialAccount = socialAccountRepository.findBySocialIdAndProvider(oAuthUserInfo.socialId(), Provider.KAKAO).get();
        User user = userRepository.findById(socialAccount.getId()).get();
        assertAll(
                () -> assertThat(tokenPair).isEqualTo(expectedTokenPair),
                () -> assertThat(socialAccount.getUserId()).isNotNull(),
                () -> assertThat(socialAccount.getSocialId()).isEqualTo(oAuthUserInfo.socialId()),
                () -> assertThat(socialAccount.getProvider()).isEqualTo(oAuthUserInfo.provider()),
                () -> assertThat(user.getNickname()).isEqualTo(oAuthUserInfo.nickname()),
                () -> assertThat(user.getProfileUrl()).isEqualTo(oAuthUserInfo.profileImageUrl())
        );
    }
}
