package com.swyp8team2.auth.presentation.filter;

import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.UnauthorizedException;

import java.util.Objects;

public class HeaderTokenExtractor {

    public static final String BEARER = "Bearer ";

    public String extractToken(String authorization) {
        if (Objects.isNull(authorization) || !authorization.startsWith(BEARER)) {
            throw new UnauthorizedException(ErrorCode.INVALID_AUTH_HEADER);
        }
        return authorization.substring(BEARER.length());
    }
}
