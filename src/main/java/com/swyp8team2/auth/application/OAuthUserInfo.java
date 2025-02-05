package com.swyp8team2.auth.application;

import com.swyp8team2.auth.domain.Provider;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;

import java.util.Map;

public record OAuthUserInfo(String socialId, String email) {

    public static OAuthUserInfo of(Provider provider, Map<String, Object> attributes) {
        switch (provider) {
            case KAKAO:
                return ofKakao(attributes);
            default:
                throw new InternalServerException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private static OAuthUserInfo ofKakao(Map<String, Object> attributes) {
        String socialId = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = String.valueOf(kakaoAccount.get("email"));
        return new OAuthUserInfo(socialId, email);
    }
}
