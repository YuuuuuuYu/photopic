package com.swyp8team2.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Provider {

    KAKAO("kakao");
//    NAVER("naver"),;

    private final String registrationId;

    public static Provider of(String registrationId) {
        return Arrays.stream(Provider.values())
                .filter(provider -> provider.registrationId.equals(registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(registrationId + "에 해당하는 SocialType이 없습니다."));
    }
}
