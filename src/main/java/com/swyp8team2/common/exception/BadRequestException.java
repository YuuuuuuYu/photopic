package com.swyp8team2.common.exception;

public class BadRequestException extends ApplicationException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
