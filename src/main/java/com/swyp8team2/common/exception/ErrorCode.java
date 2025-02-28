package com.swyp8team2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //400
    USER_NOT_FOUND("존재하지 않는 유저"),
    INVALID_ARGUMENT("잘못된 파라미터 요청"),
    REFRESH_TOKEN_MISMATCHED("리프레시 토큰 불일치"),
    REFRESH_TOKEN_NOT_FOUND("리프레시 토큰을 찾을 수 없음"),
    INVALID_REFRESH_TOKEN_HEADER("잘못된 리프레시 토큰 헤더"),
    MISSING_FILE_EXTENSION("확장자가 누락됨"),
    UNSUPPORTED_FILE_EXTENSION("지원하지 않는 확장자"),
    EXCEED_MAX_FILE_SIZE("파일 크기 초과"),
    POST_NOT_FOUND("존재하지 않는 게시글"),
    DESCRIPTION_LENGTH_EXCEEDED("게시글 설명 길이 초과"),
    INVALID_POST_IMAGE_COUNT("게시글 이미지 개수 오류"),
    NOT_POST_AUTHOR("게시글 작성자가 아님"),
    POST_ALREADY_CLOSED("이미 마감된 게시글"),
    INVALID_GUEST_HEADER("잘못된 게스트 토큰 헤더"),
    FILE_NAME_TOO_LONG("파일 이름이 너무 김"),

    //401
    EXPIRED_TOKEN("토큰 만료"),
    INVALID_TOKEN("유효하지 않은 토큰"),
    INVALID_AUTH_HEADER("잘못된 인증 헤더"),
    OAUTH_LOGIN_FAILED("소셜 로그인 실패"),

    //500
    INTERNAL_SERVER_ERROR("서버 내부 오류"),
    INVALID_INPUT_VALUE("잘못된 입력 값"),
    SOCIAL_AUTHENTICATION_FAILED("소셜 로그인 실패"),
    POST_IMAGE_NAME_GENERATOR_INDEX_OUT_OF_BOUND("이미지 이름 생성기 인덱스 초과"),
    IMAGE_FILE_NOT_FOUND("존재하지 않는 이미지"),
    POST_IMAGE_NOT_FOUND("게시글 이미지 없음"),
    SHARE_URL_ALREADY_EXISTS("공유 URL이 이미 존재"),

    //503
    SERVICE_UNAVAILABLE("서비스 이용 불가"),
    ;

    private final String message;
}
