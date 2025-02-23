package com.swyp8team2.common.exception;

public class ServiceUnavailableException extends ApplicationException {

    public ServiceUnavailableException(ErrorCode errorCode) {
        super(errorCode);
    }
}
