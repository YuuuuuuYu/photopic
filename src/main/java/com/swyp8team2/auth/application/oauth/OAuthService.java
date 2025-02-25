package com.swyp8team2.auth.application.oauth;

import com.swyp8team2.auth.application.oauth.dto.KakaoAuthResponse;
import com.swyp8team2.auth.application.oauth.dto.OAuthUserInfo;
import com.swyp8team2.common.config.KakaoOAuthConfig;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private static final String BEARER = "Bearer ";

    private final KakaoOAuthConfig kakaoOAuthConfig;
    private final KakaoOAuthClient kakaoOAuthClient;

    public OAuthUserInfo getUserInfo(String code, String redirectUri) {
        try {
            KakaoAuthResponse kakaoAuthResponse = kakaoOAuthClient.fetchToken(tokenRequestParams(code, redirectUri));
            log.info("getUserInfo kakaoAuthResponse: {}", kakaoAuthResponse);
            return kakaoOAuthClient
                    .fetchUserInfo(BEARER + kakaoAuthResponse.accessToken())
                    .toOAuthUserInfo();
        } catch (Exception e) {
            log.error("소셜 로그인 실패", e);
            throw new InternalServerException(ErrorCode.SOCIAL_AUTHENTICATION_FAILED);
        }
    }

    private MultiValueMap<String, String> tokenRequestParams(String authCode, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthConfig.clientId());
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);
        params.add("client_secret", kakaoOAuthConfig.clientSecret());
        return params;
    }
}
