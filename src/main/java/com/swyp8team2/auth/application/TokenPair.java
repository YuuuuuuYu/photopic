package com.swyp8team2.auth.application;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
