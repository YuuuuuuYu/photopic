package com.swyp8team2.common.exception;

public class ForbiddenException extends ApplicationException {

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
