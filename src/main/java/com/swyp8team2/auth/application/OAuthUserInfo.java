package com.swyp8team2.auth.application;

import com.swyp8team2.auth.domain.Provider;
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
                throw new IllegalArgumentException(provider + "에 해당하는 OAuthUserInfo가 없습니다.");
        }
    }

    private static OAuthUserInfo ofKakao(Map<String, Object> attributes) {
        String socialId = String.valueOf(attributes.get("id"));
        return new OAuthUserInfo(socialId);
    }
}
