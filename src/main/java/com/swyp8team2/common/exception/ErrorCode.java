package com.swyp8team2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //400
    USER_NOT_FOUND("존재하지 않는 유저"),
    INVALID_ARGUMENT("잘못된 파라미터 요청"),
    MISSING_FILE_EXTENSION("확장자가 누락됨"),
    UNSUPPORTED_FILE_EXTENSION("지원하지 않는 확장자"),
    EXCEED_MAX_FILE_SIZE("파일 크기 초과"),

    //401
    EXPIRED_TOKEN("토큰 만료"),
    INVALID_TOKEN("유효하지 않은 토큰"),
    INVALID_AUTH_HEADER("잘못된 인증 헤더"),
    OAUTH_LOGIN_FAILED("소셜 로그인 실패"),

    //500
    INTERNAL_SERVER_ERROR("서버 내부 오류"),
    INVALID_INPUT_VALUE("잘못된 입력 값"),

    //503
    SERVICE_UNAVAILABLE("서비스 이용 불가");

    private final String message;
}
