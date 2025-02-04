package com.swyp8team2.auth.application;

import com.swyp8team2.auth.domain.Provider;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class OAuthUserInfo {

    private final String socialId;

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
        return new OAuthUserInfo(socialId);
    }
}
