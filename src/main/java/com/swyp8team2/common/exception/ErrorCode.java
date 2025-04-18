package com.swyp8team2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //400
    USER_NOT_FOUND("존재하지 않는 유저입니다."),
    INVALID_ARGUMENT("잘못된 파라미터 요청입니다."),
    REFRESH_TOKEN_MISMATCHED("리프레시 토큰이 불일치합니다."),
    REFRESH_TOKEN_NOT_FOUND("리프레시 토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN_HEADER("잘못된 리프레시 토큰 헤더입니다."),
    MISSING_FILE_EXTENSION("확장자가 누락됐습니다."),
    UNSUPPORTED_FILE_EXTENSION("지원하지 않는 확장자입니다."),
    EXCEED_MAX_FILE_SIZE("파일 크기가 초과했습니다."),
    POST_NOT_FOUND("존재하지 않는 게시글입니다."),
    DESCRIPTION_LENGTH_EXCEEDED("게시글 설명 길이가 초과했습니다."),
    INVALID_POST_IMAGE_COUNT("게시글 이미지 개수가 범위를 벗어났습니다."),
    NOT_POST_AUTHOR("게시글 작성자가 아닙니다."),
    POST_ALREADY_CLOSED("이미 마감된 게시글입니다."),
    FILE_NAME_TOO_LONG("파일 이름이 너무 깁니다."),
    ACCESS_DENIED_VOTE_STATUS("투표 현황 조회 권한이 없습니다."),
    COMMENT_NOT_FOUND("존재하지 않는 댓글입니다."),
    VOTE_NOT_FOUND("존재하지 않는 투표입니다."),
    NOT_VOTER("투표자가 아닙니다."),

    //401
    EXPIRED_TOKEN("토큰이 만료됐습니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    INVALID_AUTH_HEADER("잘못된 인증 헤더입니다."),

    //403
    FORBIDDEN("권한 없음"),

    //404
    NOT_FOUND("리소스를 찾을 수 없음"),

    //500
    INTERNAL_SERVER_ERROR("서버 내부 오류 발생"),
    INVALID_INPUT_VALUE("잘못된 입력 값입니다."),
    SOCIAL_AUTHENTICATION_FAILED("소셜 로그인이 실패했습니다."),
    POST_IMAGE_NAME_GENERATOR_INDEX_OUT_OF_BOUND("이미지 이름 생성기 인덱스 초과"),
    IMAGE_FILE_NOT_FOUND("존재하지 않는 이미지입니다."),
    POST_IMAGE_NOT_FOUND("게시글 이미지가 없습니다."),
    SHARE_URL_ALREADY_EXISTS("공유 URL이 이미 존재합니다."),

    //503
    SERVICE_UNAVAILABLE("서비스 이용 불가"),
    ;

    private final String message;
}
