package com.swyp8team2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //common
    INVALID_ARGUMENT("잘못된 파라미터 요청"),
    INTERNAL_SERVER_ERROR("서버 내부 오류"),
    INVALID_INPUT_VALUE("잘못된 입력 값"),

    //auth
    EXPIRED_TOKEN("토큰 만료"),
    INVALID_TOKEN("유효하지 않은 토큰"),
    INVALID_AUTH_HEADER("잘못된 인증 헤더"),
    OAUTH_LOGIN_FAILED("소셜 로그인 실패"),
    ACCESS_DENIED("접근 권한 없음"),

    //user
    USER_NOT_FOUND("존재하지 않는 유저"), ;

    private final String message;
}
