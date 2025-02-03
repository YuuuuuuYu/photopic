package com.swyp8team2.common.exception;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
