package com.swyp8team2.auth.application.oauth.dto;

import com.swyp8team2.auth.domain.Provider;

public record OAuthUserInfo(
        String socialId,
        String profileImageUrl,
        String nickname,
        Provider provider
) {
}
