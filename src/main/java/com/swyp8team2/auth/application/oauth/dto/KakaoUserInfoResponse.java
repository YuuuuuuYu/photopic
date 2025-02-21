package com.swyp8team2.auth.application.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.swyp8team2.auth.domain.Provider;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        String id,
        KakaoAccount kakaoAccount
) {

    public OAuthUserInfo toOAuthUserInfo() {
        return new OAuthUserInfo(
                id,
                kakaoAccount.profile().profileImageUrl(),
                kakaoAccount.profile().nickname(),
                Provider.KAKAO
        );
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
            Profile profile
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Profile(
            String nickname,
            String profileImageUrl
    ) {}
}
