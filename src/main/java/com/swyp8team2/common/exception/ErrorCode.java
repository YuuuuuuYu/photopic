package com.swyp8team2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //common
    INVALID_ARGUMENT("잘못된 파라미터 요청"),
    INTERNAL_SERVER_ERROR("서버 내부 오류"),

    //auth
    EXPIRED_TOKEN("토큰 만료"),
    INVALID_TOKEN("유효하지 않은 토큰"),
    INVALID_AUTH_HEADER("잘못된 인증 헤더");

    private final String message;
}
