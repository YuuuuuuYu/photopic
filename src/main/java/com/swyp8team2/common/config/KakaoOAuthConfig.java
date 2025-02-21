package com.swyp8team2.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.kakao")
public record KakaoOAuthConfig(
        String authorizationUri,
        String clientId,
        String clientSecret,
        String redirectUri,
        String[] scope,
        String userInfoUri
) {
}
