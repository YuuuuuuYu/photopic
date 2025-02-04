package com.swyp8team2.common.exception;

public class InternalServerException extends ApplicationException {

    public InternalServerException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
