package com.swyp8team2.auth.application.jwt;

import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;

import java.util.Objects;

public record TokenPair(
        String accessToken,
        String refreshToken
) {

    public TokenPair {
        if (Objects.isNull(accessToken) || Objects.isNull(refreshToken)) {
            throw new InternalServerException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
