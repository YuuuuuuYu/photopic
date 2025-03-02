package com.swyp8team2.auth.application.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.swyp8team2.auth.domain.Provider;

import java.util.Objects;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        String id,
        KakaoAccount kakaoAccount
) {

    public OAuthUserInfo toOAuthUserInfo() {
        String profileImageUrl;
        String nickname;
        if (Objects.isNull(kakaoAccount.profile())) {
            profileImageUrl = null;
            nickname = null;
        } else {
            profileImageUrl = kakaoAccount.profile().profileImageUrl();
            nickname = kakaoAccount.profile().nickname();
        }
        return new OAuthUserInfo(
                id,
                profileImageUrl,
                nickname,
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
